#!/usr/bin/env python2.7
import time, socket, os, uuid, sys, kazoo, logging, signal, utils, random
from election import Election
from utils import MASTER_PATH
from utils import TASKS_PATH
from utils import DATA_PATH
from utils import WORKERS_PATH

class Client:

    def __init__(self,zk):
        self.zk = zk
	
    def submit_task(self):
		#TO COMPLETE
		
	#REACT to changes on the submitted task..				   
    def task_completed(self,data,stat):
  	  	#TO COMPLETE
	
    def submit_task_loop(self):
        while True:
            self.submit_task()

if __name__ == '__main__':
    zk = utils.init()    
    client = Client(zk)
    client.submit_task_loop()
    while True:
        time.sleep(1)

