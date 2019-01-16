#!/usr/bin/env bash

DIR=$(dirname "${BASH_SOURCE[0]}")
TMPLDIR="${DIR}/templates"

CONFIG_FILE="${DIR}/exp.config"

config() {
    if [ $# -ne 1 ]; then
        echo "usage: config key"
        exit -1
    fi
    local key=$1
    cat ${CONFIG_FILE} | grep -E "^${key}=" | cut -d= -f2
}

info() {
    local message=$1
    echo >& 2 "["$(date +%s:%N)"] ${message}"
}

log() {
    if [[ $(config verbose) -eq 1 ]]
    then
	local message=$1
	echo >& 2 "["$(date +%s:%N)"] ${message}"
    fi
}

k8s_create() {
    if [ $# -ne 2 ] && [ $# -ne 3 ]; then
        echo "usage: k8s_create template.yaml context [id]"
        exit -1
    fi
    local template=$1
    local context=$2
    local id=${3:-0} # default id is 0
    local file=${template}-${id}
    local pull=$(config pull-image)
    local image=$(config image)

    # create final template
    cat ${template} |
        sed s,%ID%,${id},g |
	sed s,%IMAGE%,${image},g |
	sed s,%PULL_IMAGE%,${pull},g |
        sed s,%CONTEXT%,${context},g |
	sed s,%BUCKET%,$(config bucket),g |
	sed s,%BUCKET_KEY%,$(config bucket_key),g |
	sed s,%BUCKET_SECRET%,$(config bucket_secret),g \
            >${file}

    # create pod
    kubectl --context="${context}" create -f ${file} >&/dev/null

    local pod_name=$(k8s_pod_name ${file})
    local pod_status="NotFound"

    # loop until pod is running
    while [ "${pod_status}" != "Running" ]; do
        sleep 1
        pod_status=$(k8s_pod_status ${context} ${pod_name})
	info "pod ${pod_name} status ${pod_status}"
    done
    info "pod ${pod_name} running at ${context}"
}

k8s_delete() {
    if [ $# -ne 2 ] && [ $# -ne 3 ]; then
        echo "usage: k8s_delete template.yaml context [id]"
        exit -1
    fi
    local template=$1
    local context=$2
    local id=${3:-0} # default id is 0
    local file=${template}-${id}
    local pod_name=$(k8s_pod_name ${file})
    local pod_status="Running"

    # loop until pod is down
    while [ "${pod_status}" != "NotFound" ]; do
        kubectl --context="${context}" delete pod ${pod_name} \
            --grace-period=0 --force \
            >&/dev/null
        sleep 1
        pod_status=$(k8s_pod_status ${context} ${pod_name})
    done
    info "pod ${pod_name} deleted at ${context}"
}

k8s_create_all_pods(){
    local context=$(config context)
    local service=$(cat ${CONFIG_FILE} | grep -ioh "/.*:" | sed s,[/:],,g)
    local proxy=""
    for i in $(seq 1 $(config nodes))
    do	
	k8s_create ${TMPLDIR}/${service}.yaml.tmpl ${context} ${i}
    done    
}

k8s_delete_all_pods() {
    local context=$(config context)
    kubectl --context=${context} delete pods --all \
	    --grace-period=0 --force \
	    2>/dev/null &

    # wait for all pods to terminate
    while [ "${empty}" != "1" ]; do
        empty=$(kubectl --context=${context} get pods 2>&1 |
		    grep "No resources found" |
		    wc -l |
		    xargs echo
	     )
    done
}

k8s_pod_name() {
    if [ $# -ne 1 ]; then
        echo "usage: k8s_pod_name file"
        exit -1
    fi
    local file=$1
    grep -E "^  name: " ${file} | awk '{ print $2 }'
}

k8s_pod_status() {
    if [ $# -ne 2 ]; then
        echo "usage: k8s_pod_status context pod_name"
        exit -1
    fi
    local context=$1
    local pod_name=$2
    kubectl --context="${context}" get pod ${pod_name} 2>&1 |
        grep -oE "(Running|Completed|Terminating|NotFound)"
}

k8s_clean_all(){
    k8s_delete_all_pods $(config context) >&/dev/null
    gsutil rm -r gs://$(config bucket)/* >&/dev/null
    info "cleaned"
}

k8s_get_service(){
    local context=$(config context)
    local service=$(echo $(config image) | grep -ioh "/.*:" | sed s,[/:],,g)
    local proxy=$(kubectl --context="${context}" get svc ${service} -o yaml | grep ip | awk '{print $3}')
    while [ "${proxy}" == "" ]; do
	kubectl --context="${context}" apply -f ${TMPLDIR}/${service}-service.yaml.tmpl
	proxy=$(kubectl --context="${context}" get svc ${service} -o yaml | grep ip | awk '{print $3}')
        sleep 1
    done
    info "service ${service} @ ${proxy}"
    echo ${proxy}
}

get_proxy(){
    if [ "$(config local)" == "false" ]
    then
    	proxy=$(k8s_get_service)
    else
	proxy="localhost:8080"
    fi
    echo ${proxy}
}
