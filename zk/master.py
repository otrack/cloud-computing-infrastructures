#!/usr/bin/env python2.7
import time, socket, os, uuid, sys, kazoo, logging, signal, utils
from election import Election
from utils import MASTER_PATH
from utils import TASKS_PATH
from utils import DATA_PATH
from utils import WORKERS_PATH

class Master:
    #initialize the master
    def __init__(self,zk):
        self.master = False
        self.zk = zk
        ##complete initialization...
		
    
    #assign tasks 				   
    def assign(self,children):
 	   #todo....
                
if __name__ == '__main__':
    zk = utils.init()
    master = Master(zk)
    while True:
        time.sleep(1)
