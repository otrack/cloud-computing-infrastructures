# Coordination in practice with Apache ZooKeeper

Modern computing infrastructures consist of very large clusters of commodity servers.
Inside a cluster, failures are norm.
For instance, Google [reports]( http://www.cnet.com/news/google-spotlights-data-center-inner-workings) that "in each cluster’s first year, it’s typical that 1,000 individual machine failures will occur".
To ensure high-availability of services under such circumstances, it is necessary to take actions, in particular to mask/hide/tolerate the failures of nodes in the most transparent manner.
The most common approach to tackle this challenge is to implement redundancy and appropriate failure-handling mechanisms.

In this practical, we will implement fault-tolerance techniques in a distributed setting.
Your objective is to build a *dependable master/worker architecture* that execute *tasks* dispatched by *clients*.
To this end, you will leverage [Apache ZooKeeper](zookeeper.apache.org), a scalable and fault-tolerant coordination service.

**Outline.**
First, we learn the basics of Apache Zookeeper, its internal and how to set-up and start the service.
Second, we introduce zk-shell, a convenient tool to interactively dialog with ZooKeeper.
We also learn how to use Kazoo, a powerful Python library to drive ZooKeeper.
Further, we present the leader election, a key building block of our construction.
Finally, we describe the core of this project, that is the master/worker architecture, and explain how to implement it.

You will have to complete all the items marked by **[TASK]** to fulfill successfully this practical.
Items marked **[OPT]** are optional.

## 1. Pre-requisites

Some knowledge of the Python programming language are mandatory.
There are plenty of very good tutorials online, and you should be able to learn the basics very quickly.
A good tutorial is [here](http://www.learnpython.org/en/).
We rely on Python 2.7, the most recent version of the 2.x series. 

## 1.1 ZooKeeper

ZooKeeper is a fault-tolerant coordination kernel.
It is at the core of the [Hadoop](http://hadoop.apache.org/) stack, a suite of softwares to mine BigData.
A coordination kernel is a type of service that allows a set of machines to synchronize in order to successfully complete a task.
ZooKeeper offers an API similar to a file system with some convenient additions such as watches, sequential and ephemeral nodes.

**[TASK]** Read the slides available [here](https://www.usenix.org/legacy/event/atc10/tech/slides/hunt.pdf) and presenting and overview of the ZooKeeper service.

**[TASK]** Following the official [guidelines](https://zookeeper.apache.org/doc/r3.1.2/zookeeperStarted.html), install ZooKeeper in stand alone mode.
Start the coordination service.

## 1.2 ZK-Shell

A common tool to inspect ZooKeeper is *zk-shell*, an interactive command line interpreter.
This interpreter is available via the [following](https://github.com/rgs1/zk_shell) repository on GitHub.
You can follow the indication given in *README.md* to install it with *pip*.

**[TASK]** Using zk-shell, create the permanent paths */master*, */tasks*, */data* and */workers*.

Hint: follow the examples given [here](https://github.com/rgs1/zk_shell#usage).

ZK-Shell is a useful tool to debug and interacting live with a running ZooKeeper service. 
However, to implement more complex scenarios and algorithms, we will rely on the Kazoo's Python bindings, as described next.

## 1.3. Kazoo

We use [kazoo](http://kazoo.readthedocs.org/en/latest/index.html), a Python library for interacting with ZooKeeper.
Kazoo offers a convenient API to perform CRUD operations (Create, Read, Update and Delete), some complex 'ZooKeeper recipes', e.g., barrier, locks, and [watchers](http://kazoo.readthedocs.org/en/latest/api/recipe/watchers.html).
All details are given in the [API documentation](http://kazoo.readthedocs.org/en/latest/api.html).
Read the documentation carefully.

**[TASK]** Try the examples given in the [online documentation](http://kazoo.readthedocs.org/en/latest/basic_usage.html) and be sure that all the libraries and dependencies are correctly installed.

**[TASK]** Try the *kazoo_example.py* on your machine.

## 2. Leader Election

In distributed computing, electing a leader is a classical way to *cooordinate* a set of processes.
When the leader fails, the system starts a new round of election until a leader is elected.
In formal terms, a leader election is a *distributed task* that allows to distinguish a node among others.
We define it as a function *election()* that returns a boolean, indicating whether the local process is the leader or not.
As expected, in each election, one node is designed as leader.

In this section, you are required to implement a leader election protocol.
ZooKeeper allows us to implement such an abstraction by exploiting the mechanisms of [watch](https://zookeeper.apache.org/doc/r3.1.2/zookeeperProgrammers.html#sc_zkDataMode_watches) and [sequential znode](https://zookeeper.apache.org/doc/r3.1.2/zookeeperProgrammers.html#Sequence+Nodes+--+Unique+Naming).
A recipe in pseudo-code using these building blocks is available [online](https://zookeeper.apache.org/doc/current/recipes.html#sc_leaderElection).

**[TASK]** Using the Kazoo library to issue CRUD operations and set watchers, complete the leader election class in *election.py*. Hint: processes should  compete on some ephemeral and sequential znode under */master*.

**[TASK]** Run several instances of *election.py*: at the end of the execution, only one leader is supposed to be elected. 

**[TASK]** Send SIGTERM to some of the running *election.py* instances. Ensure your code is correct, i.e., *(i)* at most one leader is elected, and *(ii)* eventually, a running process is elected. Hint: check an example on signal handling in Python [here](https://docs.python.org/2/library/signal.html#example).

## 3. A dependable master/worker architecture

The core service that needs to be implemented in this project consists in the construction of a dependable task processing service.
This service is realized by mean of a master/worker architecture.
It works as follows.
One or more clients connected to the ZooKeeper service and submit tasks.
The master assign tasks to the workers.
The workers process its tasks.

The system must handle and take care of the different faults scenarios that can happen: failure of a master, failure of a worker (before or while it is executing a task), failure of clients before its tasks completed. 

For example, in the case of a the master node failure, a secondary master (the backup) is elected to replace it, while keeping the task processing service available for all the clients.
An overview of the master/worker architecture is given below.

<p align="center">
<img src="https://github.com/otrack/iot-hands-on/blob/master/zk/architecture.png" width="600">
</p>

## 3.1 The master and worker components

In our implementation of the master/worker architecture, we use ZooKeeper for all the communications
between clients, workers, the master and its backup(s).
Below, we list the various steps each component of the master/worker architecture should execute.

A master (and any backup) should:

1. Set a watch on **/tasks**
2. Set a watch on **/workers**
3. Participate to the master election
4. Upon a change to **/tasks** or **/workers**
  1. If not the master, skip what follows
  2. CCompute free workers
  3. Compute unassigned tasks
  4. Map unassigned tasks to free workers

A worker should:

1. Choose a random id **xxx**
2. Create **/workers/xxx** znode
3. Watch **/workers/xxx** znode
4. Get task id **yyy** upon assignment in **/workers/xxx**
5. Get task data in **/data/yyy**
6. Execute task with data
8. Delete assignment

A client should:

1. Compute a new task **xxx**
2. Submit **xxx** and its data, in respectively in **/data** and **/tasks** 
3. Watch for **xxx** completion
4. Clean-up **xxx** metadata
5. Fetch the result
6. Repeat 1


To facilitate the implementation of the above architecture, we provide in the Git repositories the skeletons of all the above components, as well as some utilities functions that can be used across all the components.
In more details, *client.py*, *worker.py* and *master.py* contain respectively the skeletons for the client, the worker and the master.
The file *utils.py* includes a simple task definition, functions to initializes the connection to ZooKeeper and stop it upon the reception of a SIGTERM signal.

**[TASK]** Complete the code of *client.py* to submit a task. Test the correctness of your implementation by listing the content of the ZK tree with zk-shell, and emulating the completion of the task. 

**[TASK]** Complete the code of *worker.py* that retrieve a task assignment and execute it by calling *utils.task*. Again, you may test your code by running a client and a worker, then simulate the assignment of the task to the worker with zk-shell.

**[TASK]** Finish the implementation of *master.py* and test the correctness of your work.

## 3.2. Fault-Tolerance

The architecture must be resilient to different fault scenarios.
Let us denote **C/W/M** the respective number of clients, workers and master in a scenario.
Your implementation should work correctly in all following scenarios:

1. **(1/1/1)** a worker or a client fails;
2. **(1/2/1)** a worker fails;
3. **(2/2/1)** workers compete in executing the tasks submitted by the clients; and 
4. **(2/2/2)** the back-up resumes the job of the master upon a failure.

**[TASK]** Provide evidences that your implementation works correctly in the 4 mentioned scenarios.
You can provide logs and detailed explanations, or use tables, etc.

**[TASK]** Consider scenario 4 and assume that the master lags (e.g., due to a long garbage-collection cycle) instead of crashing.
Detail the type of scenario you are facing and how your solution behaves.

## 3.3 ZooKeeper in Cluster Mode

Up to now, you have run ZooKeeper in standalone mode, that is with one single ZooKeeper server.
In this last section, you must configure a ZooKeeper cluster with 3 servers.
To achieve this, follow the official instructions [here](https://zookeeper.apache.org/doc/trunk/zookeeperAdmin.html#sc_zkMulitServerSetup).

Notice that you are free to deploy the 3 servers as you wish.
ZooKeeper servers may run in separate processes, separate VMs, or even better, separate machines.
For instance, you may deploy them in an Infrastructure-as-a-Service (IaaS) Cloud service of your choice.

Once the ZooKeeper servers are functional, you should test your master/worker system in this setting.

**[OPT]** What differences with the standalone mode do you observe ? 

**[OPT]** Let one of the ZooKeeper server fail; how does the system react in this scenario ? 
