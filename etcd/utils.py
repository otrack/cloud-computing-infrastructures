#!/usr/bin/env python3
import time, socket, os, uuid, sys, logging, signal, etcd3

MASTER_PATH="/master"

TASKS_PATH="/tasks"
DATA_PATH="/data"

WORKERS_PATH="/workers"

def task(data):
    logging.debug("TASK IN")
    # simulate some computation
    if isinstance(data, bytes):
        data = data.decode()
    time.sleep(float(data))
    logging.debug("TASK OUT")
    return 0

def init():
    etcdhost = "127.0.0.1" #default etcd host
    etcdport = 2379 #default etcd port
    logging.basicConfig(format='%(asctime)s %(message)s',level=logging.DEBUG)

    if len(sys.argv) == 2:
        etcdhost=sys.argv[1]
        print("Using etcd at %s"%(etcdhost))
    
    etcd = etcd3.client(host=etcdhost, port=etcdport)
                
    def signal_handler(signal, frame):
        sys.exit(0)

    signal.signal(signal.SIGINT, signal_handler)

    return etcd
