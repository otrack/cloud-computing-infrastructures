#!/usr/bin/env python3
import etcd3
import logging
logging.basicConfig(format='%(asctime)s %(message)s', level=logging.DEBUG)

import time
import socket
import os

etcd = etcd3.client(host='127.0.0.1', port=2379)

logging.info("I am '%s-%d'" %(socket.gethostname(),os.getpid()))

etcd.put('/key', 'dooot')
print(etcd.get('/key'))
etcd.delete('/key')
print(etcd.get('/key'))
