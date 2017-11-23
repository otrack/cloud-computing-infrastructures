## Purpose

Implement a [barrier](https://en.wikipedia.org/wiki/Barrier_(computer_science)) with ZooKeeper.

## Tools

Run Zookeeper in standalone mode on your machine (following [this](https://zookeeper.apache.org/doc/r3.1.2/zookeeperStarted.html) guide).

Python support for ZooKeeper: zk_shell (https://github.com/rgs1/zk_shell)

Install zk_shell using: sudo pip install zk-shell

## First Steps

Use zk_shell to create a znode /yourName

Implement a [barrier recipe](http://zookeeper.apache.org/doc/trunk/recipes.html#sc_leaderElection) in shell, starting from tree /yourName
(use --run-once and create a barrier znode in /yourName)

<!--while [ `zk-shell --run-once "ls /sutra" 52.29.223.235:8080  | grep barrier` == "barrier" ]; do echo "waiting"; sleep 1; done) -->
