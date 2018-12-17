# How consistent is your service?

The purpose of this practical is to measure the data consistency of a storage service.
Tests are conducted à la [Jespen](https://jepsen.io), that is by interrogating the service in a black box manner.
The goal is to push the evaluated system into corner cases where running conditions are difficult (e.g., high load, concurrency and/or dynamicity).

## 1. Prerequisites [15']

The test suite executes the storage service in Kubernetes (k8s), using a load balancer as a facade.
As detailed next, several parts of the test suite are already available.

The directory `templates` contains the k8s templates of the storage service.

The file `exp.config` contains the parameters of the test, coded in the form of a set of `variable=value` pairs.
Variable `cluster` is the name of the k8s  cluster to use.
The storage image to use is defined under variable `image`.
Variable `pull-image` defines the policy to use when fetching the image from a remote repository (here, DockerHub).
Variable `bucket*` holds the necessary information to access a bucket in Google storage, as explained shortly.

The variables in `exp.config` are to be used in conjunction with the configuration functions defined in `utils.sh`.
A call to `config` returns the value stored in `exp.config` for the variable passed as argument.
The script `utils.sh`. also defines utility functions to manage pods (`k8s_create`, `k8s_delete` and `k8s_delete_all_pods`) and to monitor their executions (`k8s_pod_name` and `k8s_pod_status`).

The storage service to test is the key-value store implemented in a [prior](https://github.com/otrack/cloud-computing-infrastructures/tree/master/kvstore) practical.
If you successfully completed all the questions of this practical, you may replace the value of `image` in `exp.config` appropriately.
This service is built atop the [JGroups](www.jgroups.org) group communication library.
By default JGroups relies on IP multicast to implement nodes discovery.
This communication primitive is generally disable at cloud service providers.
As a consequence, the storage system must rely on a different algorithm to implement discovery.
Below, we use an approach that consists in writing in a data bucket in the cloud provider (further details are available [here](http://www.jgroups.org/manual/html/protlist.html#d0e5404)).

**[Q]** In Google Cloud Platform (GCP), create a bucket in the Storage menu.
Notice that bucket names are global, and as a consequence you will have to use a unique name to avoid collisions. 
Under `Settings`, make your bucket backward compatible and pick a pair `(key,secret)`.
Update the file `exp.config` appropriately.

**[Q]** Test that the functions `clean_all` and `kvs_create` execcute appropriately.
In particular, a call to `kubectl --context=your_cluster logs kvstore-i` should return that `NNODES` have been created, and that they are part of the system view at each node `kvstore-i`.

## 1. Black-box testing [60']

In the question that follow, we implement the test suite per se. 
A test puts the storage system under a specific load then evaluates if the system guarantees a particular level of data consistency.
To deploy the key-value store contaiining `NNODES`, you may use function `kvs_create`.
Notice that the first time this function is called it may take some time, as it requests a new public IP address from the cloud provider for the service `kvstore-service`.

**[Q]**  Complete function `test_read_my_write` to execute a sequence of write-then-read operations on the data store.
In this test, you should ensure that every new write operation is properly seen by a follow-up read.

**[Q]** Complete function `test_causality` to implement a test in which a writer repeatedly access keys `k1` then `k2`, while concurrently a reader retrieves `k2` then `k1`.

**[Q]** Modify your test to concurrently add and/or remove nodes in the system.

