# A distributed sum computation atop Infinispan.

## Context

Systems such as social networks, search engines or trading platforms operate geographically distant sites
that continuously generate streams of events at high-rate.
Such events can be access logs to web servers, feeds of messages from participants of a social network, or financial data, among others.
The ability to timely detect trends and popularity variations is of great interest in such systems.

In this hands-on, we consider a group of nodes that each monitor a stream of integers.
Our goal is to approximate the total sum of these integers over time.

To this end, we consider a particular node among the group of nodes.
This nodes acts as the coordinator, while the rest of the nodes are workers.
The coordinator maintains the global sum.
Each worker nodes listens to its stream of updates, and maintains a local sum.
A worker nodes have some constraint that consists in an upper and a lower bound on the value.
When an update violates the constraint, the worker informs the coordinator,
which in turn asks all workers to send their local view of the sum.
Then, the coordinator recomputes the global sum and updates the constraints at each node.

## Tasks

Your task in this hands-on is to complete the existing code available under src/main/java.
To this end, you should search in the .java files the TODO
tags that will instruct precisely you what to do.

This hands-on is built with [Apahe Maven](https://maven.apache.org).
