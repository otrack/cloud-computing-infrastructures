#!/usr/bin/env python3

import time, socket, os, uuid, sys, logging, utils, signal
import etcd3

etcdhost = "127.0.0.1" #default etcd host
if len(sys.argv) == 2:
    etcdhost = sys.argv[1]
    print("Using etcd at %s" % (etcdhost))
        
logging.basicConfig()
etcd = etcd3.client(host=etcdhost, port=2379)

# Delete all keys under the main prefixes
etcd.delete_prefix("/workers")
etcd.delete_prefix("/tasks")
etcd.delete_prefix("/data")
etcd.delete_prefix("/master")
