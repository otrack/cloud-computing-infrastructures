#!/usr/bin/env python3
import time, socket, os, uuid, sys, logging, signal, utils, random
from etcd3 import events
from election import Election
from utils import MASTER_PATH
from utils import TASKS_PATH
from utils import DATA_PATH
from utils import WORKERS_PATH

class Client:

    def __init__(self, etcd):
        self.etcd = etcd
	
    def submit_task(self):
        # create a task
        self.task_over = False
        self.task_uuid = str(uuid.uuid4())
        logging.debug("Creating task " + str(self.task_uuid))
        
        # Create task data
        self.etcd.put(DATA_PATH + "/" + self.task_uuid, str(random.random()).encode())
        
        # Submit task
        self.etcd.put(TASKS_PATH + "/" + self.task_uuid, b'')
        logging.debug("Task " + str(self.task_uuid) + " submitted")
        
        # Wait for the result by watching the task
        self.wait_for_completion()
        
        # clean-up
        self.etcd.delete(TASKS_PATH + "/" + self.task_uuid)
        self.etcd.delete(DATA_PATH + "/" + self.task_uuid)
        
    def wait_for_completion(self):
        # TODO
	
    def submit_task_loop(self):
        while True:
            self.submit_task()

if __name__ == '__main__':
    etcd = utils.init()
    client = Client(etcd)
    client.submit_task_loop()
