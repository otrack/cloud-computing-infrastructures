#!/usr/bin/env bash

jvmargs="-Djava.net.preferIPv4Stack=true"

java -cp ${JAR}:lib/* ${jvmargs} eu.tsp.transactions.Server
