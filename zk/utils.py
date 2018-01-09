#!/usr/bin/env python2.7
import time, socket, os, uuid, sys, kazoo, logging, signal
from kazoo.client import KazooClient
from kazoo.client import KazooState
from kazoo.exceptions import KazooException

MASTER_PATH="/master"
TASKS_PATH="/tasks"
DATA_PATH="/data"
WORKERS_PATH="/workers"

def task(data):
    logging.debug("TASK IN")
    # simulate some computation
    time.sleep(float(data[0]))
    logging.debug("TASK OUT")
    return 0

def init():
    zkhost = "127.0.0.1:2181" #default ZK host
    logging.basicConfig(format='%(asctime)s %(message)s',level=logging.DEBUG)

    if len(sys.argv) == 2:
        zkhost=sys.argv[2]
        print("Using ZK at %s"%(zkhost))
    
    zk = KazooClient(hosts=zkhost)
    zk.start()
    if zk.exists(MASTER_PATH) == None:
        zk.create(MASTER_PATH, ephemeral=False)
        zk.create(TASKS_PATH, ephemeral=False)
        zk.create(DATA_PATH, ephemeral=False)
        zk.create(WORKERS_PATH, ephemeral=False)
            
    def signal_handler(signal, frame):
        zk.stop()
        sys.exit(0)

    signal.signal(signal.SIGINT, signal_handler)

    return zk

