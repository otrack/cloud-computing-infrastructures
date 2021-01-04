# A key-value store (almost) from scratch

During this lab session, we implement a key-value store (KVS), a basic forms of NoSQL data storage.
This abstraction is present in many existing modern storage systems, such as. [Dynamo](https://aws.amazon.com/dynamodb), [Cassandra](http://cassandra.apache.org), [Redis](https://redis.io), or [Infinispan](http://infinispan.org).

To distribute evenly data among nodes, we employ a consistent hashing strategy.
At a low-level, the dissemination of messages relies on [JGroups](www.jgroups.org) , a widely-used open-source group-communication library.

In what follows, we first give an overview of the JGroups library.
Then, we present the Java code base from which you will start this practical.
Further, we code the consistent strategy then the core of our realization of the KVS abstraction.
The last part of this lab refines the internal of the KVS to scale (up or down) the system.
As the system is stateful, this requires to create a mechanism to migrate data.
Finally, we assess empirically the benefits of consistent hashing over a simpler round-robin strategy to distribute data.

<!-- 
	bug with name of the KVS when all students using the same name
	unit test consistent hashing
	underline the fact that get and put should first check that the code is local
-->


## 1. The JGroups library

JGroups is a library for reliable messaging in group of distributed processes.
As most group communication library, JGroups is built upon a stack of services.
A brief description of the different stack is given in the [following](https://docs.google.com/presentation/d/134aGQK7GCI-C9M5L6Bp-jBlEN4fDUabm0kSVwBG3BUg/edit#slide=id.ge81b53032_0_0) slides (page 63 to 71).

In this practical, we make use of the default JGroups protocol stack.
We shall use extensively the following three classes:
The `Address` class is the abstraction encapsulating a node.
A `JChannel` contains a set of addresses, or `View`, that change over time.
This class allows to send message to one or more nodes in the current `View`.
It also permit to register a `Listener` to get notified of a new incoming message, or a view change.

**[Task]** To understand the basics of JGroups, follow the online tutorial available [here](http://www.jgroups.org/tutorial/index.html).

## 2. Provided code

A code base for the KVS is provided  under the `src` directory.
The management of dependencies relies on Apache Maven, and a `pom.xml` file is available at the root.
In this section, we provide a guided tour of this existing code base.

### 2.2 Application interface

A KVS implements a mutable map abstraction, that is a mapping from a set of keys to a set of values.
A call to operation `get(k)` fetches the value stored under key `k`, or `null` if no such value exists.
Operation `put(k,v)` updates the mapping, storing the new relation from `k` to `v`.
This operation returns the value stored under key `k` prior the modification.

The Java interface of the KVS is available in the `Store` class.
When a client makes a call `put(k,v)` (or `get(k)`) to the `Store` interface, the KVS decides which nodes is in charge of key `k`.
To this end, it uses, the `ConsistentHash` field named `strategy` in the `StoreImpl` class.
If the key is local, the computation is triggered at the local node.
Otherwise, a command is sent to a distant node.
This node computes the reply and sent it back to the origin the command.
Upon the reception of the reply, the return of the operation is pushed back to the client.

### 2.3 Consistent hashing 

Our KVS relies on consistent hashing to distribute data among nodes.
The file `ConsistentHash.java`is a skeleton for this strategy.
This class contains a unique method `lookup` that takes as input a key and returns the bucket storing it.
In our approach, a bucket is an `org.jgroups.Address` object, that is the address of a node in JGroups.

The skeleton of `ConsistentHash` contains two fields:
 * a list of `Address` objects named `addresses`, and 
 * a `TreeSet` named `ring` that stores the position of addresses along the ring representing the key space.
Notice that the TreeSet stores integers.
As a consequence, the size of the key space is relatively small (only `2^32` possible values).
This will nonetheless suffice for our purposes.

To create a consistent hashing strategy, the caller provides a `org.jgroup.View` object to the constructor.
This view contains the current state of the distributed system.
The addresses of the nodes in the view are available as a [list](http://www.jgroups.org/javadoc/org/jgroups/View.html#getMembers--).

The implementation of the ring is based on the fact that a JGroups address is per default a `org.jgroups.UUID`.
As a consequence, for some address `addr` the return value of `addr.hashcode()` is [portable](http://www.jgroups.org/javadoc/org/jgroups/util/UUID.html#hashCode--) across machines.
Thanks to o this observation, to add an address to the ring, we simply use its hash code.

### 2.4 Data storage

The class `StoreImpl<K,V>` is the core of our implementation.
An instance of`StoreImpl<K,V>` class has a `name`.
When a client wants to create a KVS, it creates a `StoreManager` and call the `newStore(String name)` method.
A call to this method first invokes the constructor of the `StoreImpl` class with a provided name (or `StoreManager.DEFAULT_STORE`, if no name is given).
Then, the store is initialized using the `init()` method and the object is returned to the caller.

The class `StoreTest` under the test directory contains two examples of how to use the KVS store.
The first (JUnit) test `baseOperations` creates a store, then `put` the value `1` under key `42` in the KVS.
An `assert` statement checks that this is indeed the value returned by a following `get` operation.
The `multipleStores` test is a validation of the KVS in a similar vein, but this time using three data stores.

### 2.5 Notion of commands

When our data distribution strategy informs us that data is not local, we should execute a remote call.
To simplify this part, a package of marshallable commands is provided under `org.example.kvstore.cmd`.
The `Command` class is the parent class of the other commands (`Get`, `Put` and `Reply`).
Classes `Put` and `Get` carry a put or get request for some particular key.
To reply to a request, a node uses an instance of the `Reply` class.

To create commands, the KVS makes use of a factory pattern.
The class `CommandFactory` is called with the appropriate parameters for the creation of a new command.
In the `StoreImpl` class, a field named `factory` is already available.

## 3. Implementing the key-value store

We proceed in two steps.
First, we manage local calls.
Then, we amend our code to handle the case where a call at the `Store` interface is accessing data stored in a remote node.
In what follows, we consider that a single client at a time may access the KVS.

### 3.1 Local operations on the data

**[Task]** Complete the `ConsistentHash` class to encapsulate a strategy of data distribution based on consistent hashing.

**[Task]** The `StoreImpl` class should now extend `ReceiverAdapter`.
Upon receiving a new view, the KVS assigns a new strategy to the `strategy` field.

**[Task]** Complete the `put` and `get` methods to handle the case where the local node is in charge of storing the key.
Do not forget that `put(k,v)` returns the value stored under key `k` prior the invocation.
The test `StoreTest.baseOperations` should now run with success.

### 3.2 Handling remote data

Our next step is to implement the management of remote calls.
To this end, we use the tandem [ExecutorService](https://docs.oracle.com/javase/7/docs/api/java/util/concurrent/ExecutorService.html) and [Callable](https://docs.oracle.com/javase/7/docs/api/java/util/concurrent/Callable.html).
A callable takes care of handling a remote call and answering back to the caller.
The `ExecutorService` is a pool of threads in charge of executing the `Callable` objects.

**[Task]** Add a field named `workers` to the `StoreImpl` class.
Initialize this field inside the `init` method and using `Executors.newCachedThreadPool()`.

**[Task]** Add a method `send(Address dst, Command command)` to `StoreImpl`.
This method pushes a command to `dst` by bundling it inside a JGroups message.

**[Task]** Create a private `CmdHandler` that implements `Callable<Void>` in `StoreImpl`.
To create such an handler, we pass the address of the caller as well as the command to execute.
Implement the method `receive(Message msg)`.
Upon receiving a message this method retrieves the command from the payload of the message, then submits a new `CmdHandler` for this command to the `workers`.

**[Task]** Complete the `call` method in `CmdHandler` to do the computation required by the command.
This method creates a `Reply` from the command and sends it back to the caller using the method `send(Address dst, Command command)`.
When the command is a `Put`, the local data store is modified appropriately before replying to the caller.
In the reply, the field `v` holds the current value of `k` for a `Get`, and the previous value of `k` otherwise.

To complete the KVS implementation, it remains to modify the code of `put` and `get` inherited from the `Store` interface.
In the current state, these two methods handle operations on local data only.
We now modify them to also access data stored remotely.

The approach we advocate to implement this part of the KVS is to
 1. create a future,
 2. send an appropriate command to the remote node,
 3. upon the reception of a reply, update the future, then
 4. once the future is completed return the result of the operation to the caller.

A promise, or *future*, is a shared object that holds the result of some asynchronous computation.
In our context, this result is the value stored under key `k`.
(For a call to `put`, this is the value of `k` prior to the call.)

In detail, the concurrent programming pattern we are interested in is as follows:
Let us name `caller`, the thread that executes some operation at the `Store` interface.
The caller first creates a `CompletableFuture<V>`.
Then, it sends a command corresponding to the call to the appropriate remote node.
The caller then invokes the method `get` to await a result on the future.
Later on, a reply from the remote node is received by a `CmdHandler` object.
The handler updates the future with the result of the operation using the method `complete`.
Finally, the caller is unblocked and it retrieves the result of the operation from the future.

To simplify things, we use a single `CompletableFuture` in `StoreImpl`.
This future is stored in a field named `pending`.

**[Task]** Modify the code of `StoreImpl` to complete the management of remote calls.

**[Task]** Merge the management of remote and local calls into a single call method `V execute(Command cmd)`.
This method should be synchronized to avoid concurrent accesses on the `pending` field.

**[Task]** Correct your code to handle the concurrent access to data across `CmdHandler`.
Validate your implementation of `StoreImpl` by running the method `multipleStores` in `StoreTest`.

## 4. Data migration

In this last part of the practical, we are interested in adding a data migration mechanism to the KVS.
The mechanism we envision takes place while client accesses to the data store is shut-down, i.e., the service is interrupted.

**[Task]** Propose and implement a mechanism to migrate data upon a view change (in the `viewAccepted` method).

**[Task]** Validate your implementation by creating a test named `dataMigration` in the `StoreTest` class.

**[Task]** Implement a round-robbin strategy for data distribution.
Create a test named `strategyComparison` in `StoreTest`.
This test should assess the advantage of consistent hashing over a round-robbin strategy.
(For instance, it may compare the two strategies in regard to the number of integer pairs migrated upon the addition of a node.)

## 5. Extensions (optional)

**[Task]** Create a Server class that starts the KVS and listen for incoming client requests.
A possible approach to implement this quickly is to use [Spark](http://sparkjava.com), a micro framework for web applications.

**[Task]** Create a Docker file to containerize the KVS.
Compare the consistent hashing and round-robin strategies for data distribution in the wild using Kubernetes.

