#!/bin/bash

DIR=$(dirname "${BASH_SOURCE[0]}")

if [ $# -ne 3 ]; then
    echo "usage: host:port #ncalls #nclients";
    exit 1;
fi

host=$1
ncalls=$2
nclients=$3
for i in `seq 1 ${nclients}`
do
    ${DIR}/client.sh ${host} ${ncalls} &
done
wait
