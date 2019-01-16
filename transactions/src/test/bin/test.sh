#!/usr/bin/env bash

DIR=$(dirname "${BASH_SOURCE[0]}")

source "${DIR}/bank_functions.sh"

N_ACCOUNTS=100
N_OPS=1000
N_PAR=10

random_transfer(){    
    from=$((RANDOM % N_ACCOUNTS))
    to=$((RANDOM % N_ACCOUNTS))
    amount=$((RANDOM))
    transfer ${from} ${to} ${amount}
}

if [[ "$1" == "-create" ]]
then    
    if [ "$(config local)" == "false" ]
    then
	gsutil rm -r gs://$(config bucket)/* >&/dev/null # clean bucket
    	k8s_create_all_pods
    fi
elif [[ "$1" == "-delete" ]]
then    
    if [ "$(config local)" == "false" ]
    then
    	k8s_delete_all_pods
    fi
elif [[ "$1" == "-populate" ]]
then    
    for i in $(seq 0 $((N_ACCOUNTS-1)));
    do
	create_account ${i}
    done
elif [[ "$1" == "-clear" ]]
then    
    clear_accounts
elif [[ "$1" == "-run" ]]
then
    for i in $(seq 1 ${N_OPS});
    do
	random_transfer
    done
elif [[ "$1" == "-concurrent-run" ]]
then
    for i in $(seq 1 $((N_OPS/N_PAR)));
    do
	for j in $(seq 1 ${N_PAR});
	do
	    random_transfer &
	done
	wait
    done
elif [[ "$1" == "-check" ]]
then
    total=0
    for i in $(seq 0 $((N_ACCOUNTS-1)));
    do
	balance=$(get_balance $i)
	total=$((total+balance))
    done	
    info "Total=${total}"    
fi
