# Introduction to Kubernetes

Kubernetes (for short, k8s) is a software to orchestrate containers across a cluster of machines.
This practical provides an introduction to kubernetes and explains how to access a deployment either in room B313, or in the Google Cloud Platform.

Hereafter, we assume some workable knowledge of the [Docker](https://www.docker.com) software to manage containers.
If this is not the case, and before going further, please follow steps **1-2** of the [Docker tutorial for beginners](https://github.com/docker/labs/tree/master/beginner).

## 1. What is Kubernetes? [10']

Kubernetes is an open-source platform designed to automate deploying, scaling, and operating application containers.
With Kubernetes, you are able to:

 * Deploy an applications quickly and predictably;

 * Scale an application on the fly;
 
 * Roll out new features seamlessly; and
  
 * Limit hardware usage to required resources only.

**[Q]** Read the [Why containers?](https://kubernetes.io/docs/concepts/overview/what-is-kubernetes/#why-containers) section of the Kubernetes documentation to understand the motivation behind the use of containers.

To operate a kubernetes cluster, we use the `kubectl` program.
To install this program, please read the [following](https://kubernetes.io/docs/tasks/tools/install-kubectl) guide.
In case you do not have sudoer rights on the machine you are using, you may install `kubectl` under `~/.local/bin` (or any other appropriate directory) as detailed [here](https://kubernetes.io/docs/tasks/tools/install-kubectl/#install-kubectl-binary-using-curl).
Then, add this directory to the `$PATH` variable by modifying appropriately `~/.bashrc`.
For the moment, there is no need to configure kubectl and you may skip this part of the guide.

**[Q]** Install the completion for the `kubectl` command, as explained [here](https://kubernetes.io/docs/tasks/tools/install-kubectl/#enabling-shell-autocompletion).

## 2. Learning the basics [60']

The official site of [Kubernetes](https://kubernetes.io) contains several well-written tutorials.
They provide an overview of the concepts in Kubernetes and how to use it in practice.

**[Q]** Read the official Kubernetes tutorial, then do the six [online](https://kubernetes.io/docs/tutorials/kubernetes-basics) basic modules.

## 3. Selecting a Kubernetes cluster [30']

During the next practicals, you will use a Kubernetes cluster to practice and understand the basics of cloud infrastructures.
Below, we propose you three possible choices of cluster: the *minikube* emulation program, the *Google Cloud Platform* and a cluster of *Raspberry Pis*.
The instructions below explain how to deploy a k8s cluster in each case.
(Notice that it is possible to use several k8s clusters concurrently simply by switching from one kubectl configuration to another with `kubectl config use-context`.)

Once your k8s cluster is deployed, you may need to check that everything is functional.
To this end, type `kubectl get nodes` to observe the cluster nodes.
Then, you may deploy a small application following the steps proposed in either [this](https://kubernetes.io/docs/tasks/run-application/run-stateless-application-deployment) or [that](https://cloud.google.com/kubernetes-engine/docs/quickstart) tutorial.

## 3.1. Minikube  *(beginner)*

In this base approach, we emulate a cluster on a local machine using [minikube](https://github.com/kubernetes/minikube).
Internally, the program launch a kubernetes node inside a virtual machine.
This is similar to the environment you used in the online tutorial above.

**[Q]** In room B313, the `minikube` program is already installed on every machine.
Launch the program by typing `minikube start`.
Notice that starting for the first time might take a bit of time -- this is because around 300MB are downloaded.

As a side note, do not forget:

* to *shutdown properly* the VM with `minikube stop` when closing your session.
   (Otherwise, the shared files in your home directory are not synced.)

* the `minikube --help` command lists sub-commands that may help to manage your local cluster.

**[Q]** We use docker during the practicals and consequently an access to a docker daemon is required.
Minikube runs such a daemon.
To set-up the access to this daemon, we type `eval $(minikube docker-env)` in the terminal.

## 3.2 Google Cloud Platform *(intermediate)*

Google has provided us with a Google Cloud Platform (GCP) Education Grant.
Each student has a coupon to use the GCP platform for this course.
To retrieve a coupon, follow the link provided in commentary of slide 5 in this course syllabus.
Notice that credits are limited per coupon.
Use the online [calculator](https://cloud.google.com/products/calculator/#tab=container) to approximate the resources at your disposal for the course.

Deploying a k8s cluster in GCP can be done either manually via the [console](https://console.cloud.google.com), or programmatically.
Below, the later is explained using the Google Cloud SDK and in particular the `gcloud` program.

**[Q]** Install then set-up the Google Cloud SDK by following the instructions provided [here](https://cloud.google.com/sdk/install) and [there](https://cloud.google.com/sdk/docs/initializing). 
In room B313, it is necessary to make use of the [interactive installer](https://cloud.google.com/sdk/docs/downloads-interactive).

GCP is deployed all over the world and split into regions.
The list of regions and their respective locations is available [here](https://cloud.google.com/compute/docs/regions-zones).
Each region contains one or more zones, each zone being an isolated location that contains a set of resources.
Zones exist for dependability purposes, namely if a zone fails it does not impact the others.
The fully-qualified name for a zone is of the form `<region>-<zone>`. 
For example, `us-central1-a` refers to zone `a` in region `us-central1`.

The [console](https://console.cloud.google.com) lists all the services available in the GCP cloud platform.
In this course, we focus on the Kubernetes Eengine (available under *Compute*).
To create a k8s cluster, it is necessary to define the parameters of the deployment.
These parameters include the name of the cluster, its zone, the number of nodes and their types.
For instance, the command below creates a cluster named `my_cluster` in the zone `europe-west1-b`.
This deployment is made up of one [g1-small](https://cloud.google.com/compute/docs/machine-types) machine.
(A single machine is sufficient for the beginning as we will not use many ressources.)

	gcloud container clusters create my_cluster --zone=europe-west1-b --num-nodes=1 --machine-type=g1-small

The console allows to follow the creation of the cluster under *Computer -> Kubernetes engine -> clusters*.
Once the creation is made, we need to retrieve the credentials and store them in the configuration file of kubectl.

	gcloud container clusters get-credentials my_cluster --zone=my_zone

**[Q]** Create a k8s cluster in GCP at the location of your choice.

As a side note, do not forget to *shutdown properly* the cluster when you have finished using it.
To this end, you may use either the console or type the following command line

	gcloud container clusters delete my_cluster --zone=my_zone
