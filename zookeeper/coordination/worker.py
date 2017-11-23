#!/usr/bin/env python2.7
import time, socket, os, uuid, sys, kazoo, logging, signal, utils
from election import Election
from utils import MASTER_PATH
from utils import TASKS_PATH
from utils import DATA_PATH
from utils import WORKERS_PATH

class Worker:

    def __init__(self,zk):
        self.zk = zk
        self.uuid = uuid.uuid4()
		#complete by creating and watching proper nodes...		 

    #do something upon the change on assignment    
    def assignment_change(self,atask,stat):
		#to complete

if __name__ == '__main__':
    zk = utils.init()    
    worker = Worker(zk)
    while True:
        time.sleep(1)
