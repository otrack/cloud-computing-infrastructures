## Goal

Implement a [barrier](https://en.wikipedia.org/wiki/Barrier_(computer_science)) with ZooKeeper.

## First steps

Run Zookeeper in standalone mode on your machine (following [this](https://zookeeper.apache.org/doc/r3.1.2/zookeeperStarted.html) guide).

Use [zk_shell](https://github.com/rgs1/zk_shell), a Python support for ZooKeeper.
On a Linux box, you may install it as follows:

    Install zk_shell using: sudo pip install zk-shell

## What to do next

Use *zk_shell* to create a znode `/yourName`.

Implement a [barrier recipe](http://zookeeper.apache.org/doc/trunk/recipes.html#sc_leaderElection) in shell, starting from the sub-tree `/yourName`.

*tip:* in zk-shell, the `--run-once` parameter might reveal useful.

<!--while [ `zk-shell --run-once "ls /sutra" localhost:2181 | grep barrier` == "barrier" ]; do echo "waiting"; sleep 1; done) -->
