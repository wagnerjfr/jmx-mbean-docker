# Managing Java applications running in Docker containers using JMX
This project shows an example of using  **Java Management Extensions (JMX) framework** to manage Java applications running in Docker containers.

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

-p:[JMX port number]

-t:[number of threads]

-n:[system's name]

stop

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

### 1. clone the project
```
git clone https://github.com/wagnerjfr/jmx-mbean-docker.git
```

### 2. build the Dockerfile
```
$ docker build -t jmx-sample-server .
```
### 3. start 3 containers in different terminanls

**Terminal 1**

```
$ docker run --rm --name server1 -p 10001:10000 jmx-sample-server
```

**Terminal 2**

```
$ docker run --rm --name server2 -p 10002:10000 jmx-sample-server
```

**Terminal 3**

```
$ docker run --rm --name server3 -p 10003:10000 jmx-sample-server
```

### 4. compile the project
```
cd src/
javac com/example/client/*.java com/example/server/*.java
```
### 5. run ClientMBean
```
java com.example.client.ClientMBean -p:10001 -t:15 -n:test1
java com.example.client.ClientMBean -p:10002 -t:100 -n:test2
java com.example.client.ClientMBean -p:10003 -t:53 -n:test3
```

### 6. stop ClientMBean
```
java com.example.client.ClientMBean -p:10001 stop
java com.example.client.ClientMBean -p:10002 stop
java com.example.client.ClientMBean -p:10003 stop
```
