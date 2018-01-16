## Cloud Computing Infrastructures

Cloud computing is a decade-old technology paradigm that enables massively distributed computation and storage.
This new paradigm results from the merge of three technological advances: the virtualization of computing resources, the collapse of storage costs, and the ubiquitous availability of fast networks.

Typically supported by state-of-the-art data-centers containing ensembles of networked virtual machines (VMs), the Cloud delivers Infrastructure as a Service (IaaS), Platform as a Service (PaaS), Software as a Service (SaaS), and Data as a Service (DaaS).
Using these services, enterprises may offload their computing infrastructure to right-size their expenditure and reduce the time-to-market of their new products.

With more details, the Cloud brings several key benefits to mainstream computing.
First, developers with innovative ideas for new Internet services no longer require large capital outlays in hardware to deploy their services, or human expense to operate them.
The Cloud also frees information technologies companies from low level tasks, such as setting up basic hardware and software infrastructures.
Finally, by employing IaaS and DaaS services, companies can scale up and down on an as-needed basis, paying only for what they actually use.
For all these benefits, Cloud computing is of pivotal importance today in modern software applications, and as an economical area, it displays two digits growth rate per annum since 2010.

This course studies in detail the new infrastructures that sustain the Cloud.
We first examine the principles of resources virtualization and how to deploy and orchestrate clusters of virtual machines and containers.
In a second part, we review some fundamentals of large-scale distributed systems.
These ideas are then applied in a third part to the construction of modern distributed data stores.
Such stores run on commodity servers and are able to deliver the massive performance needed by the Cloud computing services.
We study some key notions such as data consistency, data distribution, replication and indexing, and we learn how to think for scalability and fault-tolerance, two fundamentals requirements of modern distributed services.

List of practicals

1. [Basics of Kubernetes](https://github.com/otrack/cloud-computing-hands-on/tree/master/warmup)
2. [Scaling horizontally a web service](https://github.com/otrack/cloud-computing-hands-on/tree/master/scaling)
3. [Traveling in the Big Apple](https://github.com/otrack/cloud-computing-hands-on/tree/master/spark)
4. [A key-value store (almost) from scratch](https://github.com/otrack/cloud-computing-hands-on/tree/master/kvstore)
5. [A single-writer multiple-readers register in message-passing](https://github.com/otrack/cloud-computing-hands-on/tree/master/abd)
6. [Coordination in practice with Apache ZooKeeper](https://github.com/otrack/cloud-computing-hands-on/tree/master/zk)

List of lectures

1. [Introduction to cloud computing architectures](https://drive.google.com/open?id=1jejBazViLenC7e80XI1guqZ_a2xo0aEr1wUV9YvBcZ0)
2. [Software Virtualization (by Gaël Thomas)](http://www-inf.telecom-sudparis.eu/COURS/chps/paam/virtualisation/ci-virtualisation.pptx.pdf)
3. [Lock-free data structures (by Gaël Thomas)](http://www-inf.telecom-sudparis.eu/COURS/chps/paam/lock-free/ci-lock-free.pptx.pdf)
4. [Data dissemination](https://drive.google.com/open?id=1PFjyNro_eNDPgBxkUdjGH647y47g3VYLLHmCS_bOpLQ)
5. [Data distribution](https://drive.google.com/open?id=1s0LRrodaYDGN3xfGit6VR9KYeoAoeRbhELBaYFHaoDU)
6. [Shared objects & consistency](https://drive.google.com/open?id=1-Uh3iC97elXSUNvwY1G0up-JaLmj-_wV8reS1bPTe8c)
7. [Concurrency control & replication](https://drive.google.com/open?id=1UFOoTEHiyxdb0u_O37P1m9cKHT6bXEcTkdgF4mmwl3Q)
8. [Consensus & coordination kernels](https://docs.google.com/presentation/d/1jVuYezqp9AgxTaNHWIiAlw5GpgQ0SLBDPNBAQFneOys/edit?usp=sharing)
9. [Inverted Index (by Emmanuel Bernard)](https://emmanuelbernard.com/presentations/inverted-index/#)
