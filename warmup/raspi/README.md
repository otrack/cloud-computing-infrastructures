# A kubernetes fleet of Raspberry Pis

In this practical, we create a cluster of Raspberry Pis by stacking them up appropriately with some hardware support.
Then, we install [Kubernetes](kubernetes.io) to orchestrate the nodes in the cluster.

The first part of the practical details how to install the [HypriotOS](https://blog.hypriot.com) distribution on a Pi.
The second part explains how to stack-up Pis using to form the cluster.
In the last part, we deploy Kubernetes across the cluster and check that our installation is functional.

To build a cluster, you should group-up in teams of four students.
In what follows, it is assumed that you `git clone` the sources of this practical.

## 1. HypriotOS

Our first step is to install an operating system on a Raspberry Pi.
To this end, we use the *v1.7* version of *HypriotOS*, a Debian-flavored Linux for Raspberry Pi.
Below, we present an "headless" installation which does not require the use of a monitor but solely to connect the device to the network.
In terms of hardware, you will need a SD card and an ethernet cable to connect the Pi.

### 1.1 The flash software

To install an operating system on a Raspberry Pi, we follow a set of instructions based on the [flash](https://github.com/hypriot/flash) tool.

First of all, we need to download and install the tool.

	curl -O https://raw.githubusercontent.com/hypriot/flash/master/$(uname -s)/flash
	chmod +x flash
	
To configure the Pi with the `flash` tool, we first need to write a file named `occidentalis.txt`.
This file serves to define the network parameters.
In our case, it contains a single line of code defining the `hostname`.
To uniquely identiy the Pi in the network, you should pick an original name.
The theme is "sailing ships" because the Kubernetes software we use means "pilot" in greek.
For instance, 

    hostname=laperouse

### 1.2 Installing the operating system 

For the steps that follow, you will need `sudo` rights on the computer your are using.
Do not hesitate to sollicitate the help of other students or of the teaching assistant.

Plug the SD card in the computer using a USB adaptator.
Then, upload the operating system image on the SD card with the `flash` tool.
For insatnce, the command line is as follows with version `v1.7.1` of HypriotOS.

	./flash -c occidentalis.txt https://github.com/hypriot/image-builder-rpi/releases/download/v1.7.1/hypriotos-rpi-v1.7.1.img.zip

Once this is done, you may plug the SD card in the Raspberry Pi, then connect to it using `ssh`.
The default login is `pirate` and the password `hypriot`.
(Notice that you may edit `/etc/hosts` to register a specific IP address, e.g., `157.159.110.31	laperouse.local`.)

	ssh pirate@laperouse.local
	
To ease networking operations, we use the same SSH key everywhere.
The private and public parts of the key are respectively available [here](https://github.com/otrack/cloud-computing-hands-on/setup/pi) and [there](https://github.com/otrack/cloud-computing-hands-on/setup/pi.pub).
Install the public part on the Raspberry Pi and the private part on your computer.
Check that everything is working properly.

Once you manage to install a Pi, repeat the above operation for two additional ones.
Thus, in total, your team should have a total of three operational Pis.

## 2. Cluster

In what follows, we create the cluster of Raspberry Pis.

## 2.1 Base component

The first step to form the cluster is to stack-up two Pis.
To this end, you need 
* two USB mini-A cables, 
* two ARJ45 cables, and 
* a multi-pi stackable Raspberry Pi case.

A schema to assemble the Pis is available [here](https://www.modmypi.com/blog/multi-pi-assembly-guide).
At the end, you should obtain something similar to the picture below.

<p align="center">
<img src="https://www.modmypi.com/image/cache/data/rpi-products/cases/multi-pi/DSC_0358-800x609.jpg" width="400">
</p>

## 2.2 Assembling and wiring

We form a cluster using three Raspberry Pis.
Two clusters are stacked-up in a _tower_.
A tower thus contains a total of six Pis 
In a tower, we use one USB hub to power the Pis, and one ethernet hub to interconnect them.

<p align="center">
<img src="https://www.modmypi.com/image/cache/data/rpi-products/accessories/usb/hubs/naked-usb-hub-800x609.jpg" width="400">
</p>

Discuss with another student team to assemble a tower.
Stack-up the Raspberry Pis and the USB hub appropriately (this last component should be at the top).
Check that you manage to reach all the Raspberry Pis in your cluster.

## 3. Kubernetes

We now have to install the Kubernetes (k8s) software in the Raspberry Pi cluster.

The steps below take inspiration from the guildelines [here](https://blog.hypriot.com/post/setup-kubernetes-raspberry-pi-cluster) by the [hypriot](https://blog.hypriot.com) team, as well as the the official documentation [there](https://kubernetes.io/docs/setup/independent/create-cluster-kubeadm).

## 3.1 Installation

The installation of k8s requries root privileges on each Pi. 
You may retrieve them with

	sudo su -

To install Kubernetes and its dependencies, only a few commands are required. 
First, trust the kubernetes APT key by adding the official APT Kubernetes repository on every node:

	curl -s https://packages.cloud.google.com/apt/doc/apt-key.gpg | apt-key add -
	echo "deb http://apt.kubernetes.io/ kubernetes-xenial main" > /etc/apt/sources.list.d/kubernetes.list

To improve the speed of your installation, we advise to define a specific [mirror](http://www.raspbian.org/RaspbianMirrors).
This requires to comment out the `mirrordirector` line in `/etc/apt/sources.list`, then add a specific one.
For instance,

    # deb http://mirrordirector.raspbian.org/raspbian/ jessie main contrib non-free rpi
	deb http://raspbian.42.fr/raspbian/ jessie main contrib non-free rpi

Then, install `kubeadm` on each node (in parallel) as follows:

	apt-get update && apt-get install -y kubeadm

## 3.2 Bootstrapping the cluster

## 3.2.1 Master node

To bootstrap the kubernetes cluster, we need to designate an initial master node.
Choose one of your Raspberry Pis, and run the following command.

	kubeadm init --pod-network-cidr 10.244.0.0/16 --token-ttl 0

This command takes a few minutes to execute.
At the end, you should obtain a log starting with:

    Your Kubernetes master has initialized successfully!	
	...

Use the last command given in the log to connect the other nodes to the network.
This should be something of the form.

    kubeadm join --token 15c86d.c1f4b7190440deff 192.168.0.23:6443 --discovery-token-ca-cert-hash sha256:3a1ac385fd8b51e8620e768f6f8f8cc724766ebe9ada6cfe8e1e53bd37f9a768

After this command executes, you should obtain something of the form:

    Node join complete:
	...

## 3.2.2 Remote management

To manage the cluster remotely, we have to install the `kubectl` tool on your local machine.
Follow the instructions listed [here](https://kubernetes.io/docs/tasks/tools/install-kubectl) for your operating system.
Please make sure that shell autocompletion is working.

It remains to import the cluster configuration on your local machine.
To this end, copy the file `/etc/kubernetes/admin.conf` in `/home/pirate` on the master node.
Give read rights to the `pirate` user using `sudo`.
Then, on your own machine imports the kubernetes configuration as follows:

	mkdir -p $HOME/.kube
	scp -i pi pirate@laperouse.local:/home/pirate/admin.conf $HOME/.kube/config
	kubectl config set-context kubernetes
	
## 3.2.3 Virtual Network

It remains to create a network fabric to interconnect nodes.
To this end, we use [flannel](https://github.com/coreos/flannel) that offers a support for ARM (the CPU type of a Raspberry Pi).
    
	kubectl create -f ./kube-flannel.yml
	
After a minute, check that the nodes are ready by typing `kubectl get nodes`.
You should observe something of that form.

    NAME          STATUS    AGE       VERSION
    black-pearl   Ready     1h        v1.8.3
    fregate       Ready     1h        v1.8.3
    laperouse     Ready     1h        v1.8.3

To sidestep [this](https://github.com/coreos/flannel/issues/799) issue, we need to add manually a few iptable rules to each node.
To this end, you should do the following steps on each Pi.

    sudo apt install -y iptables-persistent # answer "yes" both times
    sudo iptables -P FORWARD ACCEPT
    sudo iptables -t nat -A POSTROUTING -s 10.244.0.0/16 ! -d 10.244.0.0/16 -j MASQUERADE
    sudo iptables -I FORWARD 1 -i cni0 -j ACCEPT -m comment --comment "flannel subnet"
    sudo iptables -I FORWARD 1 -o cni0 -j ACCEPT -m comment --comment "flannel subnet"
    sudo netfilter-persistent save  # save rules for next reboot.

## 3.2.3 Dashboard

Our last step is to create a convenient [dashboard](https://github.com/kubernetes/dashboard) to see the activity in the cluster.

     kubectl create -f kube-dashboard.yml 
	 
The dashboard should be operational shortly.
We access it by first proxying the API server locally, then connecting to it with our favorite browser.
(The authentification page can be skipped.)

     kubectl proxy
	 my-favorite-browser http://localhost:8001/api/v1/namespaces/kube-system/services/https:kubernetes-dashboard:/proxy

Your fleet is now ready, welcome on board, captain!

<p align="center">
<img src="https://blog.hypriot.com/images/kubernetes-setup-cluster/raspberry-pi-cluster.png" width="400">
</p>
