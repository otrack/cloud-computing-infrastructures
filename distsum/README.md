# A distributed sum computation atop Infinispan.

In this lab session, we consider a distributed set of nodes, each receiving a stream of integers.
Our end goal is to approximate the global sum of these integers using Infinispan.

## 1. Context

Systems such as social networks, search engines or trading platforms operate geographically distant sites that continuously generate streams of events at high-rate.
Such events can be access logs to a set of web servers, feeds of messages from participants to a social network, or financial data, among others.
The ability to timely detect trends and/or popularity variations is of key interest in such systems.

## 2. Problematic

In this hands-on, we consider a group of nodes that each monitor a stream of integers.
Our objective is to *approximate* the total sum of these integers over time.

In formal terms, let us denote *Time* the interval of time, and *Nodes* the set of nodes.
Then, we define *stream(t,N)* the value at time *t* of the stream at node *N*.
We aim at approximating the integral of *stream(t,N)* over both *Time* and *Nodes*

## 3. Overview of the solution

To construct the approximation, we use a master-slave distributed architecture on top of Infinispan.
More precisely, we consider a particular node among the group of nodes.
This nodes acts as the *master* node and it coordinates the process of computing the global sum.
The other nodes are *slaves*, and each receives a stream of integers.

Each slave listens to its stream of integers, and maintains a *local sum*.
A slave node also maintains a [Constraint](src/main/java/eu/tsp/distsum/Constraint.java) object.
This constrains consists of an *upper* and a *lower* bound.

The master maintains the approximation of the *global sum*.
When at some slave node, an update makes the local sum violates the constraint, i.e., the local sum is outside of the bounds, the slave informs the master.
The master then asks all the slaves to send their local value of the sum.
The coordinator recomputes the global sum and updates the constraints at the slave nodes.

**[Task]** For some node *N*, let us note respectively *l(t,N)* and *L(t,N)* the lower bound and upper bound at node *N*.
If there is no constraint violation at time *t*, define the interval where lies the approximation of the master node ? 

## 4. First steps

The present project is in Java and built with the help of [Apache Maven](https://maven.apache.org).
Maven is a build automation tool, used primarily in Java software.
This tool addresses several aspects of the lifetime of a software.
In particular, it describes how the software is built, what are its dependencies and how to package it.
More advanced functions, include testing and deployment.
The structure of this project is defined in the [pom.xml](pom.xml) at the root.

**[Task]** Before going further, read the [following](https://maven.apache.org/guides/getting-started/maven-in-five-minutes.html) Maven beginner tutorial.
This tutorial covers the basics of Maven you need to know to successfully do this lab.

The [Master](src/main/java/eu/tsp/distsum/Master.java) and [Slave](src/main/java/eu/tsp/distsum/Slave.java) classes model respectively the master and the slave nodes.
Both classes inherit from the [Node](src/main/java/eu/tsp/distsum/Node.java) class.
At core this class is a listener which receives messages from other nodes as cache notifications.

**[Task]** Create a `SimpleNode` class that sub-classes the `Node` class.
This class should simply print the message it receives by overriding the method `receiveMessage(Message msg)`.

Nodes communicate using the [Channel](src/main/java/eu/tsp/distsum/Channel.java) class, which contains a `Cache` field. 
When a node *N* registers to an instance of a `Channel` object, it sets-up a listener together with a `NodeFilter`.
By default, a listener triggers upon all the updates in the `Cache`.
The filter ensures that solely updates regarding node *N* trigger at the listener.
The [NodeFilterFactory](src/main/java/eu/tsp/distsum/NodeFilterFactory.java) implements a factory of filters.
When provided with some node identifier, it construct a `NodeFilter` for that node.

**[Task]** Complete the `Channel` class to allow the communication between nodes.

The [DistributedSum](src/test/java/eu/tsp/distsum/DistributedSum.java) class includes the basics to test your code.
It uses the [TestNG](http://testng.org/doc/index.html) framework to implements its tests.
This framework is close to the more common JUnit framework, with some advanced capabilities.

The `DistributedSum` class inherits from `MultipleCacheManagersTest` and creates initially `NUMBER_SLAVES+1` instances of the `CacheManager`class.
A `CacheManager` object is an Infinispan node.
Each such object allows to retrieve the local cache by executing the operation `getCache()`.
We advise you hereafter to set `NUMBER_SLAVES` not too far from the total number of cores available on your machine, in order to avoid saturating it.

**[Task]** Complete the `baseCommunicationTest` method in `DistributedSum` to test that your implementation of `Channel` is correct.
For instance, you may create `NUMBER_SLAVES` instances of the `SimpleNode` class and make them communicate together by sending empty messages.
You may match a node to a cache manager by using the method `manager(int i)` of `DistributedSum`.

## 5. Going further 

**[Task]** Complete the `Master` and `Slave` classes to properly send the constraints to the workers, and update their local value at each node.

**[Task]** In the `DistributedSum` class, fulfill the `distributedSumTest` method to create a complete execution of the system.
To this end, you may proceed as follows:

1. Define a constant `NUMBER_ROUNDS` in `DistributedSum`;

2. Create the master and the slave nodes, as well as their respective cache managers;

3. Define some initial constraints at the slave nodes.
Then, execute `NUMBER_ROUNDS` rounds of computation.
At each such round,  inject a new update in the slave nodes.
Check that the master holds a correct approximation of the global sum.
If this approximation is incorrect at some round, raise an exception.
