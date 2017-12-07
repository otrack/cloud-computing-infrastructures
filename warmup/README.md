# Introduction to Kubernetes

Kubernetes (for short, k8s) is a software to orchestrate containers across a cluster of machines.
This practical provides an introduction to kubernetes and explains how to access an existing deployment in room B313.

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
In case you do not have sudoer rights on the machine you are using, you may install `kubectl` under `~/.local/bin` (or any other appropriate directory).
Then, add this directory to the `$PATH` variable by modifying appropriately `~/.bashrc`.

**[Q]** Install the completion for the `kubectl` command, as explained [here](https://kubernetes.io/docs/tasks/tools/install-kubectl/#enabling-shell-autocompletion).

## 2. Learning the basics [60']

The official site of [Kubernetes](https://kubernetes.io) contains several well-written tutorials.
They provide an overview of the concepts in Kubernetes and how to use it in practice.

**[Q]** Read the official Kubernetes tutorial, then do the six [online](https://kubernetes.io/docs/tutorials/kubernetes-basics) basic modules.

## 3. Selecting a Kubernetes cluster [20']

In the next practicals, you access a Kubernetes cluster to practice some core notions of cloud infrastructures.
Below, we propose you three possible choices of cluster.
Once you have made a choice, check that your installation is functional.
To this end, you can type `kubectl get nodes` to observe all the cluster nodes.
Then, you may deploy a small application such as the nginx web server as detailed [here](https://kubernetes.io/docs/tasks/run-application/run-stateless-application-deployment).

## 3.1. Minikube  *(beginner)*

In this base approach, we emulate a cluster on the local machine using [minikube](https://github.com/kubernetes/minikube).
Internally, the program launch a kubernetes cluster inside a virtual machine.
This is identical to the environment you used in the online tutorial above.
In room B313, the `minikube` program is already installed on every machine.

**[Q]** Launch minikube by typing `minikube start`.
Notice that starting for the first time might take a bit of time -- this is because around 300MB are downloaded.

As a side note, do not forget:

* to *shutdown properly* the VM with `minikube stop` when closing your session.
   (Otherwise, the shared files in your home directory are not synced.)

* the `minikube --help` command lists sub-commands that may help to manage your local cluster.

## 3.2. B313-13 *(intermediate)*

The node B313-13 runs a kubernetes deployment.
We explain how to use it below.

**[Q]** Copy the `config` file from `configs/b313` to `~/.kube`.
Create a unique namespace for your experiment following [this](https://kubernetes.io/docs/tasks/administer-cluster/namespaces-walkthrough) guide.
Then, choose the b313 deployment by running `kubectl config use-context b313`.

A namespace isolates your usage of the Kubernetes cluster from other users.
For instance, using the file `namespace.json` provided under the `configs` directory, one create the `surcouf` namespace as follows:

    kubectl create -f namespace.json # create the surcouf namespace
	kubectl config set-context surcouf --namespace=surcouf --cluster=raspi --user=raspi-admin	
	kubectl config use-context surcouf
	kubectl get nodes

## 3.3. A cluster of Raspberry Pis *(expert)*

In room B313, a cluster of Raspberry Pis runs kubernetes.
You may also use it in the following.
However, please note that there are two difficulties inherent to its use.

 * First, the access to the cluster is firewalled.
It is necessary to bypass the firewall to access the cluster.
This requires to execute a ssh tunnel, e.g., `ssh -f -i raspi/pi pirate@157.159.16.104 -L 6443:localhost:6443 -N` to access the kubernetes REST server.
In addition, the `kubectl` command should be immediately followed by `--insecure-skip-tls-verify` to bypass the certificate check.
(This is because the certificate is only valid for the IP address of the machine and not the localhost proxy.)

 * Second, Raspberry Pis use ARM chips.
As a consequence, it is necessary to build appropriate docker images for them.
This is possible by running the docker `build` command on one of the Raspberry Pis.
Another (and in fact better) appraoch is to cross-compile directly on your local machine, following the guidelines provided [here](https://blog.hypriot.com/post/setup-simple-ci-pipeline-for-arm-images/).

**[Q]** To use the Raspberry Pis deployment, copy the `config` file from `configs/raspi` to `~/.kube`.
In this file, modify the entry `server: https://157.159.16.104:6443` to `server: https://localhost:6443`.
Create a SSH tunnel as detailed above then check that the connection to the API server is working by typing `kubectl config use-context raspi` followed by `kubectl get nodes`.
You should see the 18 Raspberry Pis nodes in a `ready` state.
Finally, create a unique namespace for your experiment as detailed in Section 3.2.
