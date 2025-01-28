# A Transactional Banking System

The goal of this practical is to implement a distributed banking application.
Starting from a core of functionalities built around a small set of classes, the system is improved through successive iterations.
First, we consider a centralized Java design and implement a REST interface.
Then, the application is containerized and tested with remote clients.
In a last step, the core is distributed across multiple nodes and we use transactions to maintain data consistency.

## 1. A First Centralized Design

The bank application is built using [Maven](https://maven.apache.org).
Several parts are already provided under `src`.
In particular, the package `eu.tsp.transactions` contains the base definitions of the application.
This includes the `Bank` interface which defines the API of the banking application, and an `Account` class.
Operation `createAccount` allows the creation of a new account.
To retrieve the balance of an account, a client employs `getBalance`.
When executing `performTransfer`, a client transfers some money between two accounts.
A call to `clear` remove all the accounts in the system.
The lifecycle of the banking application is controlled via the operations `open` and `close`.

A centralized implementation of the `Bank` interface is available in the class `eu.tsp.transactions.BaseBank`.
This class is tested with the help of the [JUnit](https://junit.org/junit4) framework in `BaseBankTest` (under `src/test`).
To build a bank object, as in `BaseBankTest.setup`, the application uses a factory pattern.
Let us observe that a bank initially does not contain any account.
Operations `getBalance` and `performTransfer` should thus fail if the account identifiers passed as parameters do not exist.

**[Q11]** Run the tests available under `BaseBankTest`.
Do you think that the functionalities in `BaseBankTest` are properly covered.
If not, you may add a few methods to improve the test coverage.

### 2. REST Interface

The application is intended to be run as a micro-service, accessible via a REST interface.
To this end, the `Server` class makes use of the [Spark](https://github.com/perwendel/spark) framework.
This framework defines several functions (e.g., `put`, `post`) to expose with minimal effort an application to an HTTP client.

**[Q21]** In `Server`, implement a handler for doing a transfer in the banking system.
The handler should accept a `PUT` operation.
The call will be encoded as a path `/from/to/amount`, where `from` is the source account, `to` the destination and `amount` the money transferred between the two accounts.

**[Q22]** Test that your implementation is properly working with the help of the `curl` program.
To run the application locally, you may first package it with maven (`mvn package -DskipTests`).
Then, use the command `java -cp target/transactions-1.0.jar:target/lib/* eu.tsp.transactions.Server` to execute the server.

### 3. Containers and Further Testing

The modern approach to run micro-services is to pack them into containers.
In this practical, we rely on Docker to containerize the application.
We also makes use of Kubernetes (k8s) to execute the container in a remote cluster of machines.
To this end, several files are already available in the provided application skeleton.
A configuration file for Docker (aka., `Dockerfile`) is given under `src/main/docker`
Under `src/main/bin`, the script `run.sh` is in charge of launching the JVM with the `eu.tsp.transactions.Server` as its entry point.
This directory also contains a utility named `image.sh` to create a Docker image of the application, and to push it in `DockerHub`.

**[Q31]** Execute the banking application as a container on your local machine.
As previously, use `curl` to ensure that operations are properly executing.
(In this phase, there is no need to push the image in `DockerHub`, thus you may comment out the last two lines of `image.sh`.)

The next step is to execute and test the banking application in k8s.
Again, to simplify this task, several scripts are available under `src/test/bin`.
The scripts are tweaked with the help of the configuration file `exp.config`.

To parse a pair `key=value` in this file, we use function `config` as defined in `utils_functions.sh`.
Keys have the following meaning:
`local` indicates if the system is deployed in k8s.
`context` is the kubectl context under which the system is run and `image` defines the Docker image to use.
Key `pull-image` defines the policy to use when fetching the image from a remote repository (here, DockerHub).
The number of system nodes is set with key `nnodes`.
The `exp.config` file also mentions a number of Google storage related parameters (`bucket`, `bucket_key` and `bucket_secret`).
They are detailed later.

A client test suite is available under `src/test/bin`.
This small benchmark executes the storage service in k8s using a load balancer to expose the service.
`test.sh`, is the client frontend of the test suite.
It uses `banking_functions.sh` and `utils_functions.sh` to deploy the system and to execute a given workload.
The former script defines banking operations, while the later contains a set of utilities for k8s.
The directory `templates` contains the k8s templates of the banking system (both the system and the load balancer).

**[Q32]** Create a cluster in Google Cloud Platform to host the containers.
(As we will scale-out the system later on, it is advised to already provision a few nodes.)
Import the credentials of the cluster, and create an appropriate context with the `kubectl` command.
This last sequence of steps is recalled below:

	GCP_PROJECT=$(gcloud config list --format='value(core.project)')  
	ZONE_NAME="europe-west3-a"
	CLUSTER_NAME="cloud-computing-course"
    gcloud container clusters get-credentials ${CLUSTER_NAME} --zone ${ZONE_NAME}  
    kubectl config set-context ${CLUSTER_NAME}  --cluster=gke_${GCP_PROJECT}_${ZONE_NAME}_${CLUSTER_NAME} --user=gke_${GCP_PROJECT}_${ZONE_NAME}_${CLUSTER_NAME}  

**[Q34]** Export the Docker image in DockerHub.
Test your application using the test suite available under `src/test/bin`.

### 4. Distributing the Application

In this final step, we implement the `DistributedBanking` class and distribute the system across multiple nodes.
To achieve this, we replace the `account` variable in `BaseBanking` with a distributed mapping.
The mapping is implemented with [Infinispan](https://infinispan.org) (ISPN), a NoSQL transactional distributed storage from Red Hat.

**[Q41]** To have an overview of ISPN, read the introduction (Section 1) of the  [documentation](https://infinispan.org/docs/9.4.x/user_guide/user_guide.html).
Browse through the [online](https://infinispan.org/tutorials/simple/simple_tutorials.html) tutorials.
In particular, we advice you to have a peek at [this](https://github.com/infinispan/infinispan-simple-tutorials/blob/main/infinispan-embedded/cache-distributed/src/main/java/org/infinispan/tutorial/simple/distributed/InfinispanDistributed.java) tutorial.
At the light of the CAP impossibility result, where does this system stands?

A `Cache` in Infinispan implements a `ConcurrentMap` object as specified in the `java.util.concurrent` package.
Several operational modes are possible for the cache, synchronous, asynchronous, with or without transactions.
Depending on the configuration parameters, a cache can be local to a Java application, or spread across several nodes.

As a starter, we use a distributed asynchronous cache which runs in the same memory space as the application (embedded mode). 
This means that the `ConfigurationBuilder` to deploy the cache is written as follows

    ConfigurationBuilder builder = new ConfigurationBuilder();
    builder.clustering().cacheMode(CacheMode.DIST_SYNC);

Infinispan relies on the JGroups library to communicate.
When running locally, we use the default TCP-based configuration on JGroups (available under `src/main/resources/default-jgroups-tcp.xml`).
In this configuration, JGroups uses IP multicast to implement nodes discovery.
This communication primitive is generally disabled at cloud service providers (like GCP).
To make things work in a Kubernetes clusters, we will use a DNS-based discovery service instead.

From now on, the right JGroups configuration file is `src/main/resources/default-jgroups-google.xml`. 
This file is renamed as `jgroups.xml` when the container is deployed in a Kubernetes cluster (`local=true` in `exp.config`).
To assign a JGroups configuration in `DistributedBank`, you may use the following code:

    GlobalConfigurationBuilder gbuilder = GlobalConfigurationBuilder.defaultClusteredBuilder();
    gbuilder.transport().addProperty("configurationFile", "jgroups.xml");

**[Q42]** Create a variable `accounts` in `DistributedBank` backed by an Infinispan cache.
Implement the `Bank` interface using `put` and `get` operations, as in `BaseBank`.
Deploy the application over multiple nodes in GCP (e.g., 3).
Test the application with the scripts `test.sh`.
What do you observe and what is the root cause of this problem?

**[Q43]** ISPN relies on protobuf to marshal/un-marshal data between nodes.
To make the `Account` class understandable with protobuf, we need to anotate it.
Read the documentation [here](https://infinispan.org/docs/stable/titles/encoding/encoding.html#protostream-sci-implementations_marshalling) and add appropriate protobuf protostream annotations to `Account`.

Annotations are pre-processed at compile time by an appropriate engine.
This corresponds to the following lines in `pom.xm` which configure the Maven compiler plugin.

	<path>
	<groupId>org.infinispan.protostream</groupId>
	<artifactId>protostream-processor</artifactId>
	<version>${version.infinispan-protostream}</version>
	</path>

The engine needs some context to automate serialization.
Namely, it requires to know which protobuf file is generated, for which package, and where.

**[Q44]** To provide such information, create an `AccountSchemaBuilder` class in `tsp.transactions.distributed`.
This class should extend `org.infinispan.protostream.SerializationContextInitializer`.
Annotates the class as follows:

	@AutoProtoSchemaBuilder(
        includeClasses = {Account.class}, // List the classes to include in the schema
        schemaFileName = "account.proto", // The schema file that will be generated
        schemaFilePath = "proto/",        // Path where the schema file will be generated
        schemaPackageName = "eu.tsp.transactions" // Package name for the schema
	)

Execute again the application and verify that the error is gone.

The benchmark `test.sh` offers also the possibility to run execute multiple operations concurrently;
this is achieved with the `concurrent-run` flag.

**[Q45]** If you set a small number of bank accounts, what do you observe when concurrent operations take place?
What is the name of this anomaly?

To fix the above problem, we use the transaction support provided by Infinispan.
Change the cache object to be transactional.
This can be done programmatically as follows:

    builder.transaction().transactionMode(TransactionMode.TRANSACTIONAL).lockingMode(LockingMode.PESSIMISTIC);
	
Modify `performTransfer` to execute a transaction and check that your code is now fully functional.

**[OPT]** To make the system usable, it would be necessary to add data persistence.
Another interesting option is to replicate accounts across several nodes to improve system availability.
