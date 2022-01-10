#!/usr/bin/env python2.7
import time, socket, os, uuid, sys, kazoo, logging, signal, inspect
from kazoo.client import KazooClient
from kazoo.client import KazooState
from kazoo.exceptions import KazooException

class Election:

    def __init__(self, zk, path, func,args):
        self.election_path = path
        self.zk = zk
        self.is_leader = False
        if not (inspect.isfunction(func)) and not(inspect.ismethod(func)):
            logging.debug("not a function "+str(func))
            raise SystemError
        
        # Ensure the parent path, create if necessary
        self.path = path
        zk.ensure_path(path)        

        # Record callback function
        self.func = func

        # Start election
        zk.get_children("/master", self.ballot)
        self.node = zk.create(path+"/election", "",
            ephemeral=True, sequence=True)        
            
        
    def is_leading(self):
        return self.is_leader

    # TODO perform the election
    def ballot(self,event):
        return None
        
def hello():
    print("I won the election")
        
if __name__ == '__main__':
    zkhost = "127.0.0.1:2181" #default ZK host
    logging.basicConfig(format='%(asctime)s %(message)s',level=logging.DEBUG)
    if len(sys.argv) == 2:
        zkhost=sys.argv[1]
        print("Using ZK at %s"%(zkhost))

    zk = KazooClient(zkhost)
    zk.start()
    election = Election(zk,"/master",hello,[])
        
    while True:
        time.sleep(1)
