#!/usr/bin/env bash

DIR=$(dirname "${BASH_SOURCE[0]}")
CONFIG_FILE="${DIR}/exp.config"

DEBUG=1

config() {
    if [ $# -ne 1 ]; then
        echo "usage: config key"
        exit -1
    fi
    local key=$1
    cat ${CONFIG_FILE} | grep -E "^${key}=" | cut -d= -f2
}

log() {
    if [[ DEBUG -eq 1 ]]
    then
	local message=$1
	echo >& 2 "["$(date +%s:%N)"] ${message}"
    fi
}

k8s_create() {
    if [ $# -ne 2 ] && [ $# -ne 3 ]; then
        echo "usage: k8s_create template.yaml cluster [id]"
        exit -1
    fi
    local template=$1
    local cluster=$2
    local id=${3:-0} # default id is 0
    local file=${template}-${id}
    local pull=$(config pull-image)
    local image=$(config image)

    # create final template
    cat ${template} |
        sed s,%ID%,${id},g |
	sed s,%IMAGE%,${image},g |
	sed s,%PULL_IMAGE%,${pull},g |
        sed s,%CLUSTER%,${cluster},g |
	sed s,%BUCKET%,$(config bucket),g |
	sed s,%BUCKET_KEY%,$(config bucket_key),g |
	sed s,%BUCKET_SECRET%,$(config bucket_secret),g \
            >${file}

    # create pod
    kubectl --context="${cluster}" create -f ${file} >&/dev/null

    local pod_name=$(k8s_pod_name ${file})
    local pod_status="NotFound"

    # loop until pod is running
    while [ "${pod_status}" != "Running" ]; do
        sleep 1
        pod_status=$(k8s_pod_status ${cluster} ${pod_name})
    done
    log "pod ${pod_name} running at ${cluster}"
}

k8s_delete() {
    if [ $# -ne 2 ] && [ $# -ne 3 ]; then
        echo "usage: k8s_delete template.yaml cluster [id]"
        exit -1
    fi
    local template=$1
    local cluster=$2
    local id=${3:-0} # default id is 0
    local file=${template}-${id}
    local pod_name=$(k8s_pod_name ${file})
    local pod_status="Running"

    # loop until pod is down
    while [ "${pod_status}" != "NotFound" ]; do
        kubectl --context="${cluster}" delete pod ${pod_name} \
            --grace-period=0 --force \
            >&/dev/null
        sleep 1
        pod_status=$(k8s_pod_status ${cluster} ${pod_name})
    done
    log "pod ${pod_name} deleted at ${cluster}"
}

k8s_delete_all_pods() {
    if [ $# -ne 1 ]; then
        echo "usage: k8s_delete_all_pods cluster"
        exit -1
    fi
    cluster=$1
    kubectl --context=${cluster} delete pods --all \
	    --grace-period=0 --force \
	    2>/dev/null &

    # wait for all pods to terminate
    while [ "${empty}" != "1" ]; do
        empty=$(kubectl --context=${cluster} get pods 2>&1 |
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
        echo "usage: k8s_pod_status cluster pod_name"
        exit -1
    fi
    local cluster=$1
    local pod_name=$2
    kubectl --context="${cluster}" get pod ${pod_name} 2>&1 |
        grep -oE "(Running|Completed|Terminating|NotFound)"
}
