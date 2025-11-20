#!/usr/bin/env python3
import time, socket, os, uuid, sys, logging, signal, utils, threading
from election import Election
from utils import MASTER_PATH
from utils import TASKS_PATH
from utils import DATA_PATH
from utils import WORKERS_PATH

class Master:
    
    def __init__(self, etcd):
        self.master = False
        self.etcd = etcd
        self.election = Election(etcd, MASTER_PATH, self.set_master)
        
        # Start watching tasks and workers
        watch_thread = threading.Thread(target=self.watch_tasks, daemon=True)
        watch_thread.start()
        watch_thread = threading.Thread(target=self.watch_workers, daemon=True)
        watch_thread.start()        

    def set_master(self):
        self.master = True
        self.assign()
        
    def watch_tasks(self):
        events_iterator, cancel = self.etcd.watch_prefix(TASKS_PATH + "/")        
        for event in events_iterator:
            self.assign()                    
        
    def watch_workers(self):
        events_iterator, cancel = self.etcd.watch_prefix(WORKERS_PATH + "/")
        for event in events_iterator:            
            self.assign()                    
                    
    def assign(self):
        if self.master == False:
            return

        # Collect all pending tasks
        # TODO
        
        # Collect workers and assigned tasks
        # TODO
        
        # Compute derived sets, i.e., unassigned tasks and free workers
        # TODO
        
        # Do the assignement
        # TODO
        
if __name__ == '__main__':
    etcd = utils.init()
    master = Master(etcd)
    while True:
        time.sleep(1)
