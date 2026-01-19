# A Transactional Banking System

The goal of this practical is to implement a distributed banking application.
Starting from a core of functionalities built around a small set of classes, the system is improved through successive iterations.
First, we consider a centralized Java design and implement a REST interface.
Then, the application is containerized and tested with remote clients.
In a last step, the core is distributed across multiple nodes and we use transactions to maintain data consistency.

### 1. A First Centralized Design

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

A client test suite is available under `src/test/bin`.
This small benchmark executes the storage service in k8s using a load balancer to expose the service.
`test.sh`, is the client frontend of the test suite.
It uses `banking_functions.sh` and `utils_functions.sh` to deploy the system and to execute a given workload.
The former script defines banking operations, while the later contains a set of utilities for k8s.
The directory `templates` contains the k8s templates of the banking system (both the system and the load balancer).

**[Q32]** Create a cluster in Google Cloud Platform to host the containers.
As we will scale-out the system later on, it is advised to already provision a few nodes (use at least three e2-standard-2 machines).
Ensure that your cluster is zonal, that is deployed over a single availability zone.
Import the credentials of the cluster, and create an appropriate context with the `kubectl` command.
This last sequence of steps is recalled below:

	GCP_PROJECT=$(gcloud config list --format='value(core.project)')  
	ZONE_NAME="europe-west3-a"
	CLUSTER_NAME="cloud-computing-course"
    gcloud container clusters get-credentials ${CLUSTER_NAME} --zone ${ZONE_NAME}  
    kubectl config set-context ${CLUSTER_NAME}  --cluster=gke_${GCP_PROJECT}_${ZONE_NAME}_${CLUSTER_NAME} --user=gke_${GCP_PROJECT}_${ZONE_NAME}_${CLUSTER_NAME}  

**[Q33]** Export the Docker image in DockerHub.
Test your application using the test suite available under `src/test/bin`.

### 4. Distributing the Application

In a next step, we implement the `DistributedBanking` class and distribute the system across multiple nodes.
To achieve this, we replace the `account` variable in `BaseBanking` with a distributed data store.
The storage is implemented with [Apache Cassandra](https://cassandra.apache.org), a distributed NoSQL database designed for high availability and scalability.

**[Q41]** To have an overview of Cassandra, read the introduction of the [documentation](https://cassandra.apache.org/doc/latest/).
Browse through the [online tutorials](https://cassandra.apache.org/doc/latest/cassandra/getting-started/index.html).
In particular, we advice you to understand Cassandra's architecture and data model.
At the light of the CAP impossibility result, where does this system stand?

Cassandra provides a distributed, partitioned row store with tunable consistency levels.
The system uses a peer-to-peer architecture with no single point of failure.
Data can be automatically replicated across multiple nodes for fault tolerance.

To connect to Cassandra from our Java application, we use the Apache Cassandra Java Driver (version 4.19.2).
This driver provides a `CqlSession` object to execute CQL (Cassandra Query Language) statements.
The basic setup is as follows:

    CqlSession session = CqlSession.builder()
        .addContactPoint(new InetSocketAddress(cassandraHost, cassandraPort))
        .withLocalDatacenter("datacenter1")
        .build();

**[Q42]** As a first step, deploy a single Cassandra node in your Kubernetes cluster using the template under `src/test/bin/templates/cassandra.yaml.tmpl`.
The `cassandra-service.yaml.tmpl` creates a headless service for node discovery.

First, create the Cassandra service:

    kubectl apply -f cassandra-service.yaml

Then, deploy a single Cassandra node:

    kubectl apply -f cassandra-1.yaml

Wait for the node to be ready. You can check the status with:

    kubectl exec -it cassandra-1 -- nodetool status

**[Q43]** In `DistributedBank`, create a connection to the Cassandra node using `CqlSession`.
The implementation uses CQL prepared statements for efficient query execution.
Create a keyspace named `banking` with a replication factor of 1 (since we have only one node):

    CREATE KEYSPACE IF NOT EXISTS banking 
    WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1}

Then create a table for accounts. 
We use a composite primary key with `country_code` as the partition key:

    CREATE TABLE IF NOT EXISTS banking.accounts (
        country_code text,
        id int,
        balance int,
        PRIMARY KEY (country_code, id)
    )

The `country_code` serves as the partition key, allowing all accounts in the same country to be stored together.
This design enables efficient batch operations within a single partition.
By default, all accounts are created with `country_code = "FR"`.

Implement the `Bank` methods using CQL statements. 
Use prepared statements for better performance, such as:

    PreparedStatement insertStmt = session.prepare(
        "INSERT INTO banking.accounts (country_code, id, balance) VALUES (?, ?, ?) IF NOT EXISTS"
    );

For `performTransfer`, read the current balances, calculate the new balances, and update them.

**[Q44]** Deploy the banking application over GCP.
The pods connect to the Cassandra cluster using the `CASSANDRA_HOST` and `CASSANDRA_PORT` environment variables.
Test your general architecture by running the benchamrk suite: 

	test.sh -populate
	
Then, run the concurrent test with a small number of accounts and verify that the total sum is still zero.

    test.sh -concurrent-run
	test.sh -check

### 5. Data Replication and Consistency

**[Q51]** Now let's add data replication for fault tolerance. 
Deploy additional Cassandra nodes to create a cluster.
For instance, we may add the nodes cassandra-2 and cassandra-3 as follows:

    kubectl apply -f cassandra-2.yaml
    kubectl apply -f cassandra-3.yaml

These nodes will automatically discover and join the cluster using the **seed node** (cassandra-1).
A seed node is a well-known node that new nodes contact to learn about the cluster topology.
In our configuration (see `cassandra.yaml.tmpl`), cassandra-1 is designated as the seed node via the `CASSANDRA_SEEDS` environment variable.

Before deploying the banking application, verify that all Cassandra nodes are up and running.
Use the `nodetool status` command to check the cluster status:

    kubectl exec -it cassandra-1 -- nodetool status

You should see all three nodes listed with status "UN" (Up/Normal). 
Example output:

    Datacenter: datacenter1
    =======================
    Status=Up/Down
    |/ State=Normal/Leaving/Joining/Moving
    --  Address     Load       Tokens  Owns    Host ID                               Rack
    UN  10.244.1.5  108.45 KiB  256     ?       8d5ed9f4-89c0-4cc0-b890-e6ec2ac1fb93  rack1
    UN  10.244.2.5  108.45 KiB  256     ?       5a3d6e91-6b9c-4d2e-a1b7-3f8c9a2d1e4f  rack1
    UN  10.244.3.5  108.45 KiB  256     ?       2c7f8a3b-5e6d-4f1c-b9a2-8d4e6f7a9b1c  rack1

Wait until all nodes show "UN" status before proceeding. This may take a few minutes as nodes join the cluster.

**[Q52]** Update the keyspace replication factor to 3 to replicate data across all nodes.
Connect to one of the Cassandra nodes using `cqlsh` and alter the keyspace:

    kubectl exec -it cassandra-1 -- cqlsh

Once in the cqlsh prompt, run:

    ALTER KEYSPACE banking WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 3};

You can verify the keyspace configuration:

    DESCRIBE KEYSPACE banking;

And verify the table schema:

    DESCRIBE TABLE banking.accounts;

You can also query the database to check the data:

    SELECT * FROM banking.accounts;

This ensures that each piece of data is stored on three different nodes, providing fault tolerance.

**[Q53]** Re-execute the concurrent benchmark, as in question Q44
You should notice that the total balance across all accounts is incorrect after the test completes.
What is the name of this consistency anomaly?

**[Q54]** To fix the above problem, we need to use **conditional writes** (lightweight transactions) in Cassandra.
These use the Paxos consensus protocol to ensure that updates only succeed if the value hasn't changed since we read it.

Modify the prepared statement to include an `IF` condition:

    PreparedStatement updateConditionalStmt = session.prepare(
        "UPDATE banking.accounts SET balance = ? WHERE id = ? IF balance = ?"
    );

Then update `performTransfer` to use conditional updates.

The `IF balance = ?` condition ensures that the update only succeeds if the balance hasn't changed since we read it.
If another transaction modified the balance, the condition fails and we retry.

Deploy this corrected implementation and run the concurrent test again. 
You should now see that the total balance is preserved correctly.

Note that lightweight transactions have higher latency than regular writes (typically 4x slower) because they use the Paxos protocol.
However, they are necessary for correctness when concurrent updates to the same data are possible.

Verify that the system now handles concurrent operations correctly while providing fault tolerance through replication.
