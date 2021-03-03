# Scaling horizontally a web service

*<Disclaimer>* 
This practical assumes that you know the basics of Kubernetes and have a cluster at your disposal with `kubectl` properly set-up.
Moreover, some guidelines are only provided for [minikube](https://github.com/kubernetes/minikube), the kubernetes cluster emulator.
*</Disclaimer>* 

We are interested in scaling-up a web service named Pdfmagic.
This service allows to convert a jpg image into a pdf document (similarly to a plethora of internet sites).
The jpg file is provided by the client via a POST request to the service.

In what follows, we first get familiar with the internal of the Pdfmagic service.
Then, we create a Docker file to containerize it and  use Kubernetes to run it.
Further, we study the performance of this instance when emulating several clients.
In a last step, we scale-up the service, adding more containers to handle a higher load and observe how the system responds.

## 1. The Pdfmagic service [40']

In this section, we investigate the internals of the Pdfmagic service then create a container to hold it.

### 1.1 Overview

The source code of Pdfmagic is written in Python and provided in the `pdfmagic` directory.
This service relies on the `convert` command as well as two external Python library:
 * [delegator](https://github.com/kennethreitz/delegator.py) is a utility library that allows to call a system sub-routine.
 * [webpy](http://webpy.org) contains several building blocks to create a web server in Python. 
In a nutshell, the Pdfmagic service works as follows.
When a client connects to the service (lines 23-31 in pdfmagic.py), a welcome page is sent that contains a form with a single input field named `myfile`.
This field is of the `file` type and serves to upload the jpg image.
When Pdfmagic receives the file via a POST request (lines 33-43), the `convert` utility of Linux is called.
This utility does the actual transformation.
The client is then asked to fetch a page that holds the result.
To avoid collisions on the file names between several clients, Pdfmagic generates a random unique identifier for each request (line 37).

**[Q]** Quickly browse through the source code of the Pdfmagic service.
Where is located the pdf document on the server before being sent back to the client?

### 1.2 A container to hold the magic

Our first task is to "containerize" the Pdfmagic service.
A container is an isolated unit of computation.
In term of isolation, a container stands between a virtual machine and an (heavyweight) process.
The containerization of a program serves multiple purposes.

* First, a containerized program sees only part of the operating system (OS) resources.
This limits its activity, permitting to multiplex programs on the same hardware with guarantees on the access to resources.
For instance, a program `A` can use up to `512MB` of memory and a single core, while a program `B` has 3 cores and `1GB` of memory.

* Second, the isolation between containers also limits the security risks posed by a breach in one of them.
For instance, a malicious user that spawn a shell in the container of program `A` does not have access to the file system used by `B`.

* A third benefit is the management of dependencies.
Say for instance, that `A` uses verson `1.2.2` of library `foo`, and program `B` version `1.3.1`.
Because the two containerized programs access different file systems, each of them may contain a different version of the `foo` library.

Write a Docker file for Pdfmagic under the `pdfmagic` directory.
The Docker image should be built using `python:2` as its base image and should expose port `8080`.
(If you are blocked at this question, you may use the file available [here](https://gist.github.com/d977bdebae3c8214d9b039db62380800).)

**[Q]** Run the container locally and access it to create a pdf document from a jpg image.
Convert the logo located [here](https://formulaires.polytechnique.fr/candidatures/commun/assets/img/logo_ip_paris_vertical_blanc.jpg).
Where is the pdf document on the server?

Our next step consists in executing the Pdfmagic container on Kubernetes.
To this end, we need to proceed in two steps:

 * First, we push a Docker image for Pdfmagic to a container registry.
   This image is fetched by each pod running a container that holds the service.
   
 * Second, we create a pod to run Pdfmagic in the Kubernetes cluster.
   Here we will learn how to use a template file.

These two steps are detailed below.

## 2 Using a container registry [20']

A container registry is a very large database of container images.
The registry can be private or held by some cloud provider.
Kubernetes does not enforce the use of a particular registry.
In what follows, we use [Docker Hub](https://hub.docker.com) as this registry is free for small-scale projects.
If you already have some preferences for another registry, feel free to use it.

### 2.1 Getting an image

An image is identified in a registry with a unique identifier of the form `user_id/image_name:tag_name`.
The field  `user_id` refers to the user that created the container image `image_name`.
The field `tag` allows to manipulate multiple variations of that container, e.g., for multiple versions of the program.

In Docker Hub, there is already an image for Pdfmagic.
We can fetch and run it by typing the following command:

    docker run -p 8080:8080 0track/pdfmagic
	
When executing this command, you should see something of the form:

	Unable to find image '0track/pdfmagic:latest' locally
	latest: Pulling from 0track/pdfmagic
	...

The Docker software first checks if this image is available on your computer.
If it is not the case, the image is downloaded from the container registry.
The default behavior is to pull the container image tagged `latest`.

Notice that it is possible to pull an image without actually executing it using the following command:

    docker pull user_id/image_name:tag_name	

### 2.2 Publishing an image

It is now time to publish the Pdfmagic container image.
When using the free tier of Docker Hub, every image is publicly available.
In particular, this will make the created image available to the Kubernetes cluster.

To publish an image, you first need to register an account in [Docker Hub](https://hub.docker.com).
Fulfill the form on the right in the main page and verify your email.
Log in and create a new repository.
Choose the name `pdfmagic` for your repository and click *Create*.
Log into Docker Hub from the command line as follows:

	docker login --username=yourhubusername --email=youremail@something.else

Enter your password when prompted. 
If everything worked you will get a message of the form:

	WARNING: login credentials saved in /home/username/.docker/config.json
	Login Succeeded

Check the image ID using `docker images`.
You should see something of the form:

	REPOSITORY                   TAG                 IMAGE ID            CREATED             SIZE
	0track/pdfmagic              latest              7a6ccc196ef7        2 days ago          683MB
	pdfmagic                     latest              c552cb36eef3        6 minutes ago       683MB
	...

Before pushing, we need to tag the image.
This done as follows:

	docker tag 7a6ccc196ef7 yourhubusername/pdfmagic:latest

Then, push your image to the repository:

	docker push yourhubusername/pdfmagic
	
Your image is now publicly available for any use.

## 3. Deploying the service **[10']**

At core, there are two approaches to deploy a container in Kubernetes.
The first one consists in using the `kubectl run` command.
The second approach relies on a template file and the command `kubectl create`.
In what follows, we shall focus on the second approach being more versatile and powerful.
The file `pdfmagic.yml` inside `pdfmagic` directory contains a draft of a template for the `pdfmagic` service.

Correct the template to use your image by changing the line `image: 0track/pdfmagic:arm`.
Deploy a pod by typing `kubectl create -f pdfmagic.yml`.
Check that the pod is running by typing `kubectl get pod`.
You should observe something of the form:

	NAME       READY     STATUS              RESTARTS   AGE
	pdfmagic   0/1       ContainerCreating   0          2m

Expose the pod with `kubectl expose pod pdfmagic --name=pdfmagic --type=LoadBalancer`.
Access it to transform the logo.

## 4. Evaluating Pdfmagic **[30']**

Our end goal is to make Pdfmagic able to serve multiple clients at a time.
A first step in this direction is to understand the current behavior of Pdfmagic under various loads.
To this end, we first create a synthetic workload to exercise the system.

### 4.1 A synthetic workload

Under the `client` directory, there is a proposal of workload.
This workload consists of two shell scripts: `request.sh` and `client.sh`.

 * The script `request.sh` takes as input a Pdfmagic deployment and push a random image to it.
The size of the image is configurable (currently, 640x480 bytes).

 * The second script is `client.sh`.
This script simulates a client that pushes several successive images to Pdfmagic.
It is parameterized with the host of the Pdfmagic service and the number of times the service is called.

**[Q]** Run the workload for one client by varying the number of images that are pushed.
What is the average time taken by Pdfmagic to answer?
Plot the service time distribution of Pdfmagic for 100 successive requests.
(You may use any tool, e.g., [matplotlib](https://matplotlib.org/users/pyplot_tutorial.html), [gnuplot](www.gnuplot.info), or some spreadsheet.)

### 4.2 Multiple clients

The next step is to understand the behavior of the system when several clients access it.
To this end, a third script named `parallel.sh` is available under the `client` directory.
This script calls `client.sh` in parallel.
It takes as input 3 parameters: the Pdfmagic host, the number of clients, and the number of times each client calls the service.

**[Q]** Plot the service latency when 10 clients push concurrently 100 images.
What do you observe?

## 5. Scaling out the service **[30']**

Our previous analysis tells us that the performance of Pdfmagic degrades when clients are accessing it concurrently.
To remedy this problem, several approaches are possible.
A first approach is to *scale up* the service, that is to use a *bigger machine* to run it.
This approach is termed *scaling vertically* the service.
A second approach is to *scale out* the system, that is to use *more machines* to run it.
This is called *scaling horizontally* the service.
In what follows, we will use Kubernetes to implement the second approach by running more Pdfmagic containers in parallel.

Implementing the scale-out strategy is possible with the notion of [deployment](https://kubernetes.io/docs/concepts/workloads/controllers/deployment).
A deployment describes a set of replicas when the service to run has no mutable persistent data (i.e., it is stateless).
A template file to create a deployment of the Pdfmagic service is available under the directory `pdfmagic`.
The file is named `deploy.yml`.

**[Q]** Correct `deploy.yml` to use your Docker image and 3 pods.
Add a load balancer to balance the traffic between the pods.
Plot the service latency when 10 clients push concurrently 100 images.
Make a plot of the time distribution.
Do you think that this modification is satisfying?

**[Q]** *(optional)* Analyze the performance of a single instance of Pdfmagic.
Do you think that the idea of scaling-out the service was the right one?
What is the underlying problem with the implementation of Pdfmagic?

**[Q]** *(optional)* Transform the workload `client.sh` into a Kubernetes [job](https://kubernetes.io/docs/concepts/workloads/controllers/jobs-run-to-completion).
Run a workload with 50 parallel clients.
Plot the latency of the service along time.
