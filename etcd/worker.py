#!/usr/bin/env python3
import time, socket, os, uuid, sys, logging, signal, utils, threading
from etcd3 import events
from election import Election
from utils import MASTER_PATH
from utils import TASKS_PATH
from utils import DATA_PATH
from utils import WORKERS_PATH

class Worker:

    def __init__(self, etcd):
        self.etcd = etcd
        self.uuid = str(uuid.uuid4())
        
        # Create a lease for this worker
        self.lease = self.etcd.lease(ttl=10)
        
        # Register the worker
        logging.debug("my id is %s", self.uuid)
        self.etcd.put(WORKERS_PATH + "/" + self.uuid, b'', lease=self.lease)

        # Keep lease alive in background
        self.refresh_thread = threading.Thread(target=self.keep_lease_alive, daemon=True)
        self.refresh_thread.start()
        
        # Watch for assignments
        self.watch_thread = threading.Thread(target=self.watch_assignments, daemon=True)
        self.watch_thread.start()
        
    def keep_lease_alive(self):
        while True:
            time.sleep(5)
            logging.debug("refreshing lease " + str(self.lease.id))
            self.lease.refresh()
        
    def watch_assignments(self):
        events_iterator,_ = self.etcd.watch_prefix(WORKERS_PATH + "/" + self.uuid)        
        for event in events_iterator:
            if isinstance(event, events.PutEvent):
                task = os.path.basename(event.key)
                self.process_assignment(task.decode())
        
    def process_assignment(self, task):
        # TODO
        
if __name__ == '__main__':
    etcd = utils.init()
    worker = Worker(etcd)
    while True:
        time.sleep(1)
        
