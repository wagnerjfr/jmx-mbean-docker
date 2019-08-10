# Managing Java applications running in Docker containers using JMX
This project shows an example of using  [Java Management Extensions (JMX) framework](https://www.oracle.com/technetwork/java/javase/tech/javamanagement-140525.html) to manage Java applications running in Docker containers.

## Application ServerJMX
The Java application running in the container prints the values of its attributes **threadCount** and **systemName** in a loop of 3s interval.

### Sample output
```console
Thread Count = 10 :: System Name = System
Thread Count = 10 :: System Name = System
Thread Count = 10 :: System Name = System
Thread Count = 10 :: System Name = System
Thread Count = 10 :: System Name = System
Thread Count = 10 :: System Name = System
Thread Count = 10 :: System Name = System
Thread Count = 10 :: System Name = System
Thread Count = 10 :: System Name = System
Thread Count = 10 :: System Name = System
Thread Count = 10 :: System Name = System
Thread Count = 10 :: System Name = System
```

## Application ClientMBean
The client application can be used to change server attributes (**threadCount** and **systemName**) remotely from the host.

Arguments:

`-p:<JMX port number>`

`-t:<number of threads>`

`-n:<system's name>`

`stop`

### Sample Output
**Example 1:** `java com.example.client.ClientMBean -p:10001 -t:15 -n:test1`
```console
Thread Count = 10 :: System Name = System
Thread Count = 10 :: System Name = System
Thread Count = 10 :: System Name = System
Thread Count = 15 :: System Name = test1
Thread Count = 15 :: System Name = test1
Thread Count = 15 :: System Name = test1
```

**Example 2:** `java com.example.client.ClientMBean -p:10001 stop`
```console
Thread Count = 15 :: System Name = test1
Thread Count = 15 :: System Name = test1
Thread Count = 15 :: System Name = test1
Client requested to stop..
Thread Count = 15 :: System Name = test1
Server JMX stopped
```

## Instructions

### 1. Clone the project
```
$ git clone https://github.com/wagnerjfr/jmx-mbean-docker.git
```
Get into the folder:
```
$ cd jmx-mbean-docker
```

### 2. Build the Dockerfile
```
$ docker build -t jmx-sample-server .
```
### 3. Start 3 containers in different terminals

**Terminal 1: Server1 -> port 10001**
```
$ docker run --rm --name server1 -p 10001:10000 jmx-sample-server
```
**Terminal 2: Server2 -> port 10002**
```
$ docker run --rm --name server2 -p 10002:10000 jmx-sample-server
```

**Terminal 3: Server3 -> port 10003**
```
$ docker run --rm --name server3 -p 10003:10000 jmx-sample-server
```

### 4. Compile the project
```
cd src/
javac com/example/client/*.java com/example/server/*.java
```
### 5. Run ClientMBean
```
java com.example.client.ClientMBean -p:10001 -t:15 -n:test1
```
Expected output:
```console
Bean -p:10001 -t:15 -n:test1

Create an RMI connector client and connect it to the RMI connector server
service:jmx:rmi:///jndi/rmi://localhost:10001/jmxrmi

Waiting for notification...

Received notification:
	ClassName: javax.management.AttributeChangeNotification
	Source: com.example:type=ServerJMX
	Type: jmx.attribute.change
	Message: threadCount changed
	AttributeName: threadCount
	AttributeType: int
	NewValue: 15
	OldValue: 10

SystemName old = System
SystemName new = test1

Close the connection to the server
```
The same can be done for the other servers accessing through their ports:
```
java com.example.client.ClientMBean -p:10002 -t:100 -n:test2
java com.example.client.ClientMBean -p:10003 -t:53 -n:test3
```

### 6. Stop ClientMBean
```
java com.example.client.ClientMBean -p:10001 stop
java com.example.client.ClientMBean -p:10002 stop
java com.example.client.ClientMBean -p:10003 stop
```
