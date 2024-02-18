/*
 * Client.java - JMX client that interacts with the JMX agent. It gets
 * attributes and performs operations on the Hello MBean and the QueueSampler
 * MXBean example. It also listens for Hello MBean notifications.
 */

package com.example.client;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import com.example.server.ServerJMXMBean;

public class ClientMBean {
    private static final int PORT = 10000;

    public static void main(String[] args) throws Exception {
        final String host = System.getenv("host");
        if (host == null) {
            System.out.println("Invalid hostname, please pass it as parameter: -e host=<hostname>");
            return;
        }

        final String systemName = System.getenv("name");
        final String threadCount = System.getenv("threads");
        final String stop = System.getenv("stop");

        JMXConnector jmxc;
        try {
            System.out.println("\nCreate an RMI connector client and connect it to the RMI connector server");
            final String url = "service:jmx:rmi:///jndi/rmi://" + host + ":" + PORT + "/jmxrmi";
            JMXServiceURL serviceUrl = new JMXServiceURL(url);
            jmxc = JMXConnectorFactory.connect(serviceUrl, null);
            System.out.println(url);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return;
        }

        // Create listener
        ClientListener listener = new ClientListener();

        // Get an MBeanServerConnection
        MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();

        // Construct the ObjectName for the ServerJMX MBean
        ObjectName mbeanName = new ObjectName("com.example:type=ServerJMX");

        ServerJMXMBean mbeanProxy = JMX.newMBeanProxy(mbsc, mbeanName, ServerJMXMBean.class, true);

        // Add notification listener on ServerJMX MBean
        mbsc.addNotificationListener(mbeanName, listener, null, null);

        if (threadCount != null) {
            try {
                int val = Integer.parseInt(threadCount);
                if (val < 0) {
                    throw new RuntimeException("Cannot be negative");
                }
                mbeanProxy.setThreadCount(val);

                // Sleep for 2 seconds to have time to receive the notification
                System.out.println("\nWaiting for notification...");
                sleep(2000);
            } catch (Exception e) {
                System.out.println("Invalid number of threads " + threadCount);
            }
        }

        if (systemName != null) {
            // Get CacheSize attribute in ServerJMX MBean
            System.out.println("\nSystemName old = " + mbeanProxy.getSystemName());
            mbeanProxy.setSystemName(systemName);
            // Get CacheSize attribute in ServerJMX MBean
            System.out.println("SystemName new = " + mbeanProxy.getSystemName());
        }

        if (Boolean.parseBoolean(stop)) {
            mbeanProxy.stop();
        }

        // Close MBeanServer connection
        System.out.println("\nClose the connection to the server");
        jmxc.close();
    }

    private static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
