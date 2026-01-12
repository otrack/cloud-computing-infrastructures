# Coordination in Practice with etcd

Modern computing infrastructures consist of very large clusters of commodity servers.
Inside a cluster, failures are norm.
For instance, Google [reports](http://www.cnet.com/news/google-spotlights-data-center-inner-workings) that "in each cluster's first year, it's typical that 1,000 individual machine failures will occur".
To ensure high-availability of services under such circumstances, it is necessary to take actions, in particular to mask/hide/tolerate the failures of nodes in the most transparent manner.
The common approach to tackle such a challenge is to implement redundancy and appropriate failure-handling mechanisms.

In this practical, we will implement fault-tolerance techniques in a distributed setting.
Your objective is to build a *dependable master/worker architecture* that execute *tasks* dispatched by *clients*.
To this end, you will leverage [etcd](https://etcd.io/), a distributed, reliable key-value store for the most critical data of a distributed system.

**Outline.**
First, we learn the basics of etcd, its internal and how to set-up and start the service.
Second, we introduce etcdctl, a convenient tool to interactively dialog with etcd.
We also learn how to use python-etcd3, a powerful Python library to drive etcd.
Further, we present the leader election, a key building block of our construction.
Finally, we describe the core of this project, that is the master/worker architecture, and explain how to implement it.

You will have to complete all the items marked by **[Task]** to fulfill successfully this practical.
Items marked **[Opt]** are optional.

## 1. Pre-requisites

This practical uses the Python programming language.

## 1.1 etcd

etcd is a distributed reliable key-value store for the most critical data of a distributed system.
It is written in Go and uses the Raft consensus algorithm to manage a highly-available replicated log.
etcd is used in production by many companies including Kubernetes, which uses it as its primary datastore for cluster configuration and state.
etcd offers a key-value API with support for watch operations, lease-based key expiration, and atomic compare-and-swap operations.

**[Task]** Following the official [installation guide](https://etcd.io/docs/latest/install/), install etcd in standalone mode.
Start the etcd service.
Alternatively, you may also run it within a container, as explained [here](https://etcd.io/docs/latest/op-guide/container/).

**[Task]** Read the etcd documentation available [online](https://etcd.io/docs/latest/learning/api/) to get an overview of the etcd service API.

## 1.2 etcdctl

etcd provides a command line client called *etcdctl*.
This tool is included with the etcd installation (as well as in the container image).

**[Task]** Using etcdctl, explain how to create a key `/task/123` that stores the string `"hello, etcd!"`.

**[Task]** In light of the documentation, does the `put` operation permit to solve consensus among two clients? 
Justify your answer.

etcdctl is a useful tool to debug and interact live with a running etcd service. 
However, to implement more complex scenarios and algorithms, we will rely on python-etcd3's Python bindings, as described next.

## 1.3. python-etcd3

We use [python-etcd3](https://python-etcd3.readthedocs.io/en/latest/), a Python library to interact with etcd.
python-etcd3 offers a convenient API to perform CRUD (Create, Read, Update and Delete) operations.
All the details are given in the [API documentation](https://python-etcd3.readthedocs.io/en/latest/usage.html).
Read this documentation carefully.

**[Task]** Install python-etcd3 using pip.

**[Task]** Try some of the examples given in the [online documentation](https://python-etcd3.readthedocs.io/en/latest/usage.html) and be sure that all the libraries and dependencies are correctly installed.

## 2. Leader Election

In distributed computing, electing a leader is a classical way to *coordinate* a set of processes.
When the leader fails, the system starts a new round of election and a new leader is elected.
In formal terms, a leader election is a *distributed task* that allows to distinguish a node among others.
We define it as a function *election()* that returns a boolean, indicating whether the local process is the leader or not.
As expected, in each election, one node is designated as the leader.

In this section, you are required to implement a leader election protocol.
etcd permits to implement such an abstraction by exploiting the mechanisms of [leases](https://etcd.io/docs/latest/learning/api/#lease-api) and [watches](https://etcd.io/docs/latest/learning/api/#watch-streams).
A recipe using these building blocks involves creating keys with leases (time-to-live) and using watches to detect changes.

The leader election algorithm works as follows:
1. All the processes watch the keys created/deleted under some common prefix (e.g., `/master/candidate-XXX`)
2. Each process creates a lease.
3. Then, it inserts a key under a common prefix, attaching it the lease.
4. When a key disappears (due to lease expiration or explicit deletion), the watching process checks if it should become the leader.

**[Task]** Using the python-etcd3 library to create keys with leases and set watches, complete the leader election class in *election.py*. 

**[Task]** Run several instances of *election.py*: at the end of the execution, only one leader is supposed to be elected. 

**[Task]** Send SIGTERM to some of the running *election.py* instances. 
Ensure your code is correct, that is, *(i)* at most one leader is elected, and *(ii)* eventually, a running process is elected. 

## 3. A dependable master/worker architecture

The core service implemented in this practical is a fault-tolerant (dependable) task processing service.
This service is realized by mean of a master/worker architecture.
It works as follows:
One or more clients connected to the etcd service submit tasks.
The master assigns tasks to the workers.
The workers process these tasks and report their results to the clients.

The system must handle and take care of the different fault scenarios that may happen: 
the failure of a master, 
the failure of a worker (before or while it is executing a task), and 
the failure of a client before its tasks complete. 

For instance, in the case of a master node failure, a secondary master (the backup) is elected to replace it, while keeping the task processing service available for the clients.
An overview of the master/worker architecture is depicted below.

**[Task]** What kind of usage do you see of the master/worker type of architecture?

<p align="center">
<img src="https://github.com/otrack/cloud-computing-infrastructures/blob/master/etcd/architecture.png" width="600">
</p>

## 3.1 The master and worker components

In our implementation of the master/worker architecture, we use etcd for all the communications  between clients, workers, the master and its backup(s).
Below, we list the various steps each component of the master/worker architecture should execute.

A master (and any backup) should:

1. Watch `/tasks` prefix for new tasks.
2. Watch `/workers` prefix for worker availability.
3. Participate to the master election.
4. Upon a change to `/tasks` or `/workers`.
  1. If not the master, skip what follows.
  2. Find the free workers.
  3. Find the unassigned tasks.
  4. Map these unassigned tasks to the free workers.

A worker should:

1. Choose a random id `xxx`.
2. Create `/workers/xxx` key with a lease.
3. Watch `/workers/xxx` for assignments.
4. Task `yyy` is assigned to `xxx` when key `/workers/xxx/yyy` is created.
5. Get task data in `/data/yyy`.
6. Execute the task with its data.
8. Delete the assignment.

A client should:

1. Create a new task `xxx`.
2. Submit `xxx` and its data, in respectively `/tasks` and `/data`.
3. Watch for `xxx` completion.
4. Clean-up `xxx` metadata.
5. Fetch the result.
6. Repeat 1.


To facilitate the implementation of the above architecture, we provide in the Git repository the skeletons of all the above components, as well as some utility functions that can be used across all components.
In more details, *client.py*, *worker.py* and *master.py* contain respectively the skeletons for the client, worker, and master.
The file *utils.py* includes the definition of a (basic) task, functions to initialize the connection to etcd, and to stop it upon the reception of a SIGTERM signal.

**[Task]** Complete the code of *client.py* to submit a task. 
Test the correctness of your implementation by listing the content of the etcd keyspace with etcdctl, and emulating the completion of the task. 

**[Task]** Complete the code of *worker.py* that retrieves a task assignment and executes it by calling *utils.task*. 
Again, you may test your code by running a client and a worker, then simulate the assignment of the task to the worker with etcdctl.

**[Task]** Finish the implementation of *master.py*, then test the correctness of your code as a whole.

## 3.2. Fault Tolerance

The architecture must be resilient to different fault scenarios.
Let us denote **C/W/M** the respective number of clients, workers and master in a scenario.
Your implementation should work correctly in all the following scenarios:

1. **(1/1/1)** a worker or a client fails;
2. **(1/2/1)** a worker fails;
3. **(2/2/1)** workers compete in executing the tasks submitted by the clients; and
4. **(2/2/2)** the back-up resumes the job of the master upon a failure.

**[Task]** Provide evidences that your implementation works correctly in the 4 mentioned scenarios.
You can provide logs and detailed explanations, or use tables, etc.

**[Task]** Consider scenario 4 and assume that the master lags (e.g., due to a long garbage-collection cycle) instead of crashing.
Detail the type of scenario you are facing and how your solution behaves.

## 3.3 etcd in Cluster Mode

Up to now, you have run etcd in standalone mode, that is with one single etcd server.
In this last section, you must configure an etcd cluster with 3 servers.
To achieve this, follow the official instructions [here](https://etcd.io/docs/latest/op-guide/clustering/).

Notice that you are free to deploy the 3 servers as you wish.
etcd servers may run in separate processes, separate VMs, or even better, separate machines.
For instance, you may deploy them in an Infrastructure-as-a-Service (IaaS) Cloud service of your choice.

Once the etcd servers are functional, you should test your master/worker system in this setting.

**[Opt]** What differences with the standalone mode do you observe ? 

**[Opt]** Let one of the etcd server fail. 
How does the system react in this scenario ? 
