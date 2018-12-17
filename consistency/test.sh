#!/usr/bin/env bash

DIR=$(dirname "${BASH_SOURCE[0]}")

TMPLDIR="${DIR}/templates"
BINDIR="${DIR}"

CONFIG_FILE="${DIR}/exp.config"
NNODES=3

source ${DIR}/utils.sh

entry_point=$(kubectl get svc kvstore-service -o yaml | grep ip | awk '{print $3}') >&/dev/null

# management

clean_all(){
    k8s_delete_all_pods $(config cluster) >&/dev/null
    gsutil rm -r gs://$(config bucket)/* >&/dev/null
    log "cleaned"
}

# operations

kvs_create(){
    for i in $(seq 1 ${NNODES})
    do
	k8s_create ${TMPLDIR}/kvstore.yaml.tmpl $(config cluster) ${i}
    done    
    kubectl apply -f ${TMPLDIR}/kvstore-service.yaml.tmpl
    while [ "${entry_point}" == "" ]; do
	export entry_point=$(kubectl get svc kvstore-service -o yaml | grep ip | awk '{print $3}')
        sleep 1
    done
    log "kvs created @ ${entry_point}"
}

kvs_get(){
    if [ $# -ne 1 ]; then
        echo "usage: read key"
        exit -1
    fi
    key=$1

    log "INV get(${key}) @ ${entry_point}"
    res=$(curl -m 1 -s -X get "${entry_point}/${key}")
    log "RES get(${key}) ${res}"

    echo ${res}
}

kvs_put(){ 
    if [ $# -ne 2 ]; then
        echo "usage: put key value"
        exit -1
    fi
    key=$1
    value=$2

    log "INV put(${key}, ${value}) @ ${entry_point}"
    res=$(curl -m 1 -s -X put "${entry_point}/${key}/${value}")
    log "RES put(${key}, ${value}) ${res}"

    echo ${res}
}

# tests

test_read_my_write(){
    true # FIXME
}

test_causality(){
    true # FIXME
}

test_causality_with_elasticity(){
    true # FIXME
}

