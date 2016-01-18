## Purpose

Implement a [barrier](https://en.wikipedia.org/wiki/Barrier_(computer_science)) with ZooKeeper.

## Tools

Running instance of ZooKeeper @52.29.223.235:8080 (on Amazon AWS).

Python support for ZooKeeper: zk_shell (https://github.com/rgs1/zk_shell)

Install zk_shell using: sudo pip install zk-shell

## First Steps

Use zk_shell to create a znode /yourName

Implement a [barrier recipe](http://zookeeper.apache.org/doc/trunk/recipes.html#sc_leaderElection) in shell, starting from tree /yourName
(use --run-once and create a barrier znode in /yourName)

<!--while [ `zk-shell --run-once "ls /sutra" 52.29.122.122  | grep barrier` == "barrier" ]; do echo "waiting"; sleep 1; done) -->
