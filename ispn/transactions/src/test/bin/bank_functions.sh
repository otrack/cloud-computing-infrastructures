#!/usr/bin/env bash

DIR=$(dirname "${BASH_SOURCE[0]}")
source ${DIR}/utils_functions.sh

proxy=$(get_proxy)

create_account(){
    if [ $# -ne 1 ]; then
        echo "usage: create_account id"
        exit -1
    fi
    id=$1

    res=$(curl -m 1 -s -X post "${proxy}/${id}")
    log "create_account(${id}) @ ${proxy} -> ${res}"
}

get_balance(){
    if [ $# -ne 1 ]; then
        echo "usage: get_balance id"
        exit -1
    fi
    id=$1
    res=$(curl -m 1 -s -X get "${proxy}/${id}")    
    log "get_balance(${id}) @ ${proxy} -> ${res}"
    echo ${res}
}

transfer(){
    if [ $# -ne 3 ]; then
        echo "usage: transfer from to amount"
        exit -1
    fi
    from=$1
    to=$2
    amount=$3
    res=$(curl -m 1 -s -X put "${proxy}/${from}/${to}/${amount}")
    log "transfer(${from},${to},${amount}) @ ${proxy} -> ${res}"
}

clear_accounts(){
    if [ $# -ne 0 ]; then
        echo "usage: clear"
        exit -1
    fi
    res=$(curl -m 1 -s -X post "${proxy}/clear")
    log "create_account(${id}) @ ${proxy} -> ${res}"
}
