## Cloud Computing Infrastructures

#### Abstract.

Cloud computing is a recent technology that enables massively distributed computation and storage.
This new paradigm results from the merge of three technological advances: the virtualization of computing resources, the collapse of storage costs, and the ubiquitous availability of fast networks.

Typically supported by state-of-the-art data-centers containing ensembles of networked virtual machines, the Cloud delivers a myriad of remote digital services, including Infrastructure as a Service (IaaS), Platform as a Service (PaaS) and Software as a Service (SaaS).
Using these services, enterprises may offload their computing infrastructure to right-size their expenditure and reduce the time-to-market of their products.

The Cloud brings several key benefits to mainstream computing.
First, developers with innovative ideas for new Internet services no longer require large capital outlays in hardware to deploy their services, or human expense to operate them.
The Cloud also frees information technologies companies from low-level tasks, such as setting up basic hardware and software infrastructures.
Finally, by employing cloud services, companies can scale up and down on an as-needed basis, paying only for what they actually use.
For all these benefits, cloud computing is of pivotal importance today in modern software applications, and as an economical area, it displays a double-digit growth rate per annum for a decade.

This course studies in detail the infrastructures that support the Cloud.
We first examine the principles of resources virtualization and how to deploy and orchestrate clusters of virtual machines and containers.
In a second part, we review some fundamentals of large-scale distributed systems.
These ideas are then applied in a third part to the construction of modern distributed data stores.
Such stores run on commodity servers and are able to deliver the massive performance needed by cloud services.
We study key notions such as data consistency, data distribution, replication and indexing, and we learn how to think for scalability and fault-tolerance, two fundamentals requirements of modern distributed applications.

#### List of lectures.

1. [Introduction to cloud computing architectures](https://drive.google.com/open?id=1jejBazViLenC7e80XI1guqZ_a2xo0aEr1wUV9YvBcZ0)
2. [Data dissemination](https://drive.google.com/open?id=1PFjyNro_eNDPgBxkUdjGH647y47g3VYLLHmCS_bOpLQ)
3. [Data distribution](https://drive.google.com/open?id=1s0LRrodaYDGN3xfGit6VR9KYeoAoeRbhELBaYFHaoDU)
4. [Shared objects & consistency](https://drive.google.com/open?id=1-Uh3iC97elXSUNvwY1G0up-JaLmj-_wV8reS1bPTe8c)
5. [Protocols for data replication](https://drive.google.com/open?id=1UFOoTEHiyxdb0u_O37P1m9cKHT6bXEcTkdgF4mmwl3Q)
6. [Hardware virtualization (by Mathieu Bacou)](https://www-inf.telecom-sudparis.eu/COURS/CSC5004/lectures/hardware-virtualization.pdf)
7. [Operating system-level virtualization (by Mathieu Bacou)](https://www-inf.telecom-sudparis.eu/COURS/CSC5004/lectures/os-virtualization.pdf)
8. [Serverless computing (by Mathieu Bacou)](https://www-inf.telecom-sudparis.eu/COURS/CSC5004/lectures/serverless-computing.pdf)
9. [Coordination kernels](https://docs.google.com/presentation/d/1jVuYezqp9AgxTaNHWIiAlw5GpgQ0SLBDPNBAQFneOys/edit?usp=sharing)
10. [The Paxos protocol](https://docs.google.com/presentation/d/1-P4nD8p2uiumkISj3BZ0GFsIvnqvofG3RUxbOYgi_2g/edit?usp=sharing)
11. [Distributed transactional systems](https://docs.google.com/presentation/d/10pC5K4Sb4XG5U-CFqbPb3e9220ZcYbKLiWtxCVKSx9k/edit?usp=sharing)
12. [Inverted Index (by Emmanuel Bernard)](https://emmanuelbernard.com/presentations/inverted-index/#)
13. [Replicated Data Consistency for Large Scale Distributed Systems (by Gérald Oster)](https://github.com/otrack/cloud-computing-hands-on/raw/master/lectures/Replicated%20Data%20Consistency%20for%20Large%20Scale%20Distributed%20Systems.pdf)

#### List of practicals.

1. [Basics of Kubernetes](https://github.com/otrack/cloud-computing-hands-on/tree/master/warmup)
2. [Scaling horizontally a web service](https://github.com/otrack/cloud-computing-hands-on/tree/master/scaling)
3. [A key-value store (almost) from scratch](https://github.com/otrack/cloud-computing-hands-on/tree/master/kvstore)
4. [A single-writer multiple-readers register in message-passing](https://github.com/otrack/cloud-computing-hands-on/tree/master/abd)
5. [Virtual machine management](https://www-inf.telecom-sudparis.eu/COURS/CSC5004/practicals/vm-management.pdf)
6. [Simple container engine](https://www-inf.telecom-sudparis.eu/COURS/CSC5004/practicals/simple-container-engine.pdf)
7. [Basics of Apache OpenWhisk](https://www-inf.telecom-sudparis.eu/COURS/CSC5004/practicals/basics-openwhisk.pdf)
8. [Scaling horizontally a web service, revisited](https://www-inf.telecom-sudparis.eu/COURS/CSC5004/practicals/scaling-revisited.pdf)
9. [Coordination in practice with Apache ZooKeeper](https://github.com/otrack/cloud-computing-hands-on/tree/master/zk)
10. [A transactional banking system](https://github.com/otrack/cloud-computing-infrastructures/blob/master/transactions/README.md)
11. [Traveling in the Big Apple](https://github.com/otrack/cloud-computing-hands-on/tree/master/spark)
