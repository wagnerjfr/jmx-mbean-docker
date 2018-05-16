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

    private static final String HOST = "localhost";
    private static final String SEPARATOR = ":"; 

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println(String.format("Some args must be specified. Example: java ClientMbean -p%s10000 -t%s3 -n%stest", SEPARATOR));
            System.exit(0);
        }

        int port = 0;
        boolean hasPort = false;
        int threadCount = -1;
        boolean hasThreadCount = false;
        String systemName = null;
        boolean stop = false;

        for (String arg : args) {
            if (arg.contains("-p")) {
                try {
                    port = Integer.parseInt(arg.substring(arg.indexOf(SEPARATOR)+1));
                    hasPort = true;
                }
                catch (Exception e) {
                    System.err.println("Error value " + arg);
                    System.exit(1);
                }
            }
            else if (arg.contains("-t")) {
                try {
                    threadCount = Integer.parseInt(arg.substring(arg.indexOf(SEPARATOR)+1));
                    hasThreadCount = true;
                }
                catch (Exception e) {
                    System.err.println("Error value " + arg);
                    System.exit(1);
                }
            }
            else if (arg.contains("-n")) {
                systemName = arg.substring(arg.indexOf(SEPARATOR)+1);
            }
            else if (arg.contains("stop")) {
                stop = true;
            }
        }

        if (!hasPort) {
            System.out.println("A port, example: -p:10000, must be definied.");
        }

        System.out.println("\nCreate an RMI connector client and connect it to the RMI connector server");
        String url = "service:jmx:rmi:///jndi/rmi://" + HOST + ":" + port + "/jmxrmi";
        JMXServiceURL serviceUrl = new JMXServiceURL(url);
        JMXConnector jmxc = JMXConnectorFactory.connect(serviceUrl, null);
        System.out.println(url);

        // Create listener
        ClientListener listener = new ClientListener();

        // Get an MBeanServerConnection
        MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();

        // Construct the ObjectName for the ServerJMX MBean
        ObjectName mbeanName = new ObjectName("com.example:type=ServerJMX");

        ServerJMXMBean mbeanProxy = JMX.newMBeanProxy(mbsc, mbeanName, ServerJMXMBean.class, true);

        // Add notification listener on ServerJMX MBean
        mbsc.addNotificationListener(mbeanName, listener, null, null);

        if (hasThreadCount) {
            mbeanProxy.setThreadCount(threadCount);

            // Sleep for 2 seconds to have time to receive the notification
            System.out.println("\nWaiting for notification...");
            sleep(2000);
        }

        if (systemName != null) {
            // Get CacheSize attribute in ServerJMX MBean
            System.out.println("\nSystemName old = " + mbeanProxy.getSystemName());
            mbeanProxy.setSystemName(systemName);
            // Get CacheSize attribute in ServerJMX MBean
            System.out.println("SystemName new = " + mbeanProxy.getSystemName());
        }

        if (stop) {
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
