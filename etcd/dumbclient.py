#!/usr/bin/env python3

import time, socket, os, uuid, sys, logging, utils, signal
import etcd3

# A client should
# 1 - submit tasks
# 2 - Watch for task completion
# repeat 1

class Client():

    def __init__(self, etcd):
        self.etcd = etcd

    def submit_task(self):
        task_uuid = str(uuid.uuid4())
        self.etcd.put("/tasks/task-" + task_uuid, b'')
        
        # Watch for completion
        events_iterator, cancel = self.etcd.watch("/tasks/task-" + task_uuid)
        
        for event in events_iterator:
            if event.type == 'PUT':
                value = event.value
                if value and value != b'':
                    print("Task %s completed" % (value.decode()))
                    cancel()
                    break
			
etcdhost = "127.0.0.1" #default etcd host
if len(sys.argv) == 2:
    etcdhost = sys.argv[1]
    print("Using etcd at %s" % (etcdhost))
        
logging.basicConfig()
etcd = etcd3.client(host=etcdhost, port=2379)

def signal_handler(signal, frame):
    sys.exit(0)

signal.signal(signal.SIGINT, signal_handler)

client = Client(etcd)
client.submit_task()
