## **** WARNING PROJECT UNDER CONSTRUCTION ****

### Project 3 - Weeks 12 and 13: 25/03 - 05/04

### DHT Cooperative Mirroring with Quorum-based consistency protocol

The project assumes that you have read and understood the following sections in the Distributed system book:
- Section 5.2 - Distributed hash tables (DHT) (Naming)
- Section 6.3 - Decentralized algorithm (Coordination)
- Section 7.5 - Quorum-based protocols (Consistency and Replication)

Therefore the project builds on exercise 7 - Chord Distributed Hash Table (https://github.com/selabhvl/dat110public/tree/master/week7/ChordDHT)

### Description of project
This implementation is based on synchronous communication by using the Java RMI remote procedure call (RPC)

A DHT can be used to build a cooperative mirroring peer-to-peer distributed system in which multiple providers of content cooperate to store and serve each others’ data.

![](assets/cooperative.png)

The idea behind cooperative mirroring is that a node can replicate its content among other nodes in the p2p system. In the DHT implementation, this replication is done dynamically where nodes can be assigned a file if the hash value (keyid) of a replica of this file maps to the hash value (id) of the node by using the rule (id >= keyid).

We use a simple implementation where a node, once it has successfully joined a chord ring, distributes it file by multicasting the hash of the file name and the initial content to the ring. 
- Replicas are first created by calling createReplicaFiles(filename) in the FileManager.
- Next replicas are distributed to the nodes in the ring by resolving the replica name from the node
- When a remote node is mapped to a replica, a remote rpc is invoked on createFileInNodeLocalDirectory(sendercontent, replicahashid) of the remote node. If successful, a replica file with the name - replicahashid and content - sendercontent are created in the remote node. 

A file can then be looked up by any client from any node in the ring (see the fig). The resolution for a file starts from this node.

The current implementation of the project allows the simulation of chord ring in a single machine environment.
To use multi computer simulation requires two slight modifications. 
1. In the Node class constructor - change the setNodeIP to use the IP address of the machine (setNodeIP(InetAddress.getLocalHost().getHostAddress())) instead of setNodeIP(nodename). 
2. In the Util class, use registry = LocateRegistry.getRegistry(ipaddress, StaticTracker.PORT) and comment out registry = LocateRegistry.getRegistry(StaticTracker.PORT);

When these changes are made, you can then start the chord project on different machines.

The ChordDHT is provided to you as a complete system that allows you to test read and write requests from any client and also test the quorum-based consistency protocol.

To get started you need to download and import the ChordDHT and the QuorumAlgorithm projects into your eclipse IDE. You can find the project here:https://github.com/selabhvl/dat110public/tree/master/week12/ChordDHT
The ChordDHT project is divided into the following packages:

### ChordDHT Project organisation

##### no.hvl.dat110.rpc.interfaces
- ChordNodeInterface: The interface class where remote functions for each node are defined.
- QuorumInterface: Interface for the quorum algorithm

##### no.hvl.dat110.node
- Node: implements the ChordNodeInterface with its attributes and methods. For example, a node has an IP address, an identifier (hash of the IP address), finger table list, key id list, and so on. In addition, a node can be called to lookup any key id. Therefore, the method findSuccessor(keyid) and findHighestPredecessor(keyid) are directly implemented in node. In addition, the predecessor P of the successor S of node N can be notified that it has a new predecessor N, if P is between N and S after a remote call. This is implemented as P.notifySuccessor(N) and it's usually called during stabilize ring operation.
- Message: 
- Operations:
- OperationType: 
- MajorityPermission:

- NodeInformation: used to print out information about the status of the node (IP, ID, finger table entries, current keys)

##### no.hvl.dat110.node.client.test
- NodeClientReader: 
- NodeClientWriter:
- NodeClientTester: class that can be run to lookup a keyid of a resource and obtain the node that is responsible for it. The file id needs to be specified in the class.
- ChordRingTest: 

##### no.hvl.dat110.chordoperations
This package contains five classes responsible for specific chord protocols: 
- StabilizeRing: Checks whether a node P's successor is still valid. If P's successor has predecessor Q which is different from P, then P needs to accept Q as its new successor and Q needs to accept P as its new predecessor (via notifySuccessor)
- CheckPredecessor: runs periodically and makes a remote call to a node's predecessor and checks whether it's still valid. If call fails, predecessor is removed
- FixFingerTable: runs periodically to update the finger table for each node.
- UpdateSuccessor: runs periodically to set the first pointer of the finger table to the correct successor
- JoinRing: calls once when the node is being created to determine whether to join an existing ring or to start a new ring. It uses initial addresses from the StaticTracker class to determine who and where to join a ring.
- LeaveRing: runs periodically until the specified sleep time after which it the node leaves the ring

##### no.hvl.dat110.file
- FileMapping: used to simulate cooperative mirroring by using random files and distributing those files among existing chord nodes.

##### no.hvl.dat110.rpc
- ChordNodeContainer: This is the 'server' for the node where the registry is started and where the binding of the remote stub object for the Node is done. In addition, all periodic chord operations are started in this class currently.

- StaticTracker: class that defines the ip addresses of possible active nodes in a ring. In addition, the port for the registry is specified in this class.

##### no.hvl.dat110.util
- Hash: implements hash function method and converts the hash value to big integer. Also, it implements a custom modulo 2^mbit function for testing purposes (where mbit = 4).
- Util: contains various utility methods for obtaining registry or performing conversion.

### Running the ChordDHT program
1. Specify one valid IP address (multi-machine testsing) or process name (single machine testing) in the StaticTracker class (This class starts and creates the ring)
2. Specify the ttl value to decide how long the node should be alive. If you want the program to run forever, set the run forever variable to true in the ChordNodeContainer. Or increase the sleep time to make it run longer.
3. Run the ChordNodeContainer class
4. Several ChordNodeContainer instances can then be launched. Each must specify addresses of active ChordNodeContainers that are running currently
5. 



### QuorumAlgorithm project organisation

##### no.hvl.dat110.mutexprocess
- MutexProcess: 
- ProcessContainer:
- Message:
- Config:
- MajorityPermission:
- Operations:

##### no.hvl.dat110.interfaces
- ProcessInterface:
- OperationType:

##### no.hvl.dat110.util
- Util:

##### no.hvl.dat110.clients
- Processes (1-10) creating read and write operations to test quorum-based consistency protocol



There are two major tasks that you will implement in this project

#### Task 1 - implement a quorum-based consistency protocol

You should use the QuorumAlgorithm template to implement the algorithm correctly. 
- implement a quorum-based consistency protocol on the template provided. Test and make sure the algorithm works correctly by using the unit tests provided with the project
***more descriptions to follow

#### Task 2 - Integrate the Quorum algorithm into the ChordDHT

- Integrate the quorum-based implementation with the chord implementation. Test your implementation using the unit tests provided with the chord project
*** more descriptions to follow

#### Task 3 - Implement Read and Write clients
Your task here is to implement clients that can send a read or write request to the chord ring by contacting any of the active node.
