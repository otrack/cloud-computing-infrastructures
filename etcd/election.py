#!/usr/bin/env python3
import time, socket, os, uuid, sys, logging, signal, inspect, etcd3, threading
from threading import Event

class Election:

    def __init__(self, etcd, path, func):
        self.election_path = path
        self.etcd = etcd
        self.is_leader = False
        if not (inspect.isfunction(func)) and not(inspect.ismethod(func)) and func is not None:
            logging.debug("not a function "+str(func))
            raise SystemError
        self.func = func
        self.is_leader = False

        # Start watching for leadership changes
        self.watch_thread = threading.Thread(target=self.watch_election, daemon=True)
        self.watch_thread.start()

        # Create a lease for this candidate (TTL set to 10s)
        # TODO
        
        # Create a unique key for this candidate with a timestamp-based sequence
        self.candidate_id = str(int(time.time() * 1000000)) + "-" + str(uuid.uuid4())        
        self.my_key = self.election_path + "/candidate-" + self.candidate_id
        logging.debug("my key is %s", self.my_key)

        # Put the key with the lease
        # TODO
        
        # Keep lease alive in background
        self.refresh_thread = threading.Thread(target=self.keep_lease_alive, daemon=True)
        self.refresh_thread.start()
        
        
    def is_leading(self):
        return self.is_leader
        
    def keep_lease_alive(self):
        # TODO
            
    def check_leadership(self):
        candidates = []
        for value, metadata in self.etcd.get_prefix(self.election_path + "/candidate-"):
            candidates.append(metadata.key.decode())
        
        candidates.sort()
        logging.debug("candidates are: %s", candidates)

        if candidates[0] == self.my_key:
            logging.debug("I am the new leader")
            self.is_leader = True
            if self.func is not None:
                self.func()
                    
    def watch_election(self):
        # TODO
        
if __name__ == '__main__':
    etcdhost = "127.0.0.1" #default etcd host
    logging.basicConfig(format='%(asctime)s %(message)s',level=logging.DEBUG)
    if len(sys.argv) == 2:
        etcdhost=sys.argv[1]
        print("Using etcd at %s"%(etcdhost))
    
    etcd = etcd3.client(host=etcdhost, port=2379)
    
    election = Election(etcd, "/master", None)
    
    while True:
        time.sleep(1)
