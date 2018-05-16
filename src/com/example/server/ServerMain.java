package com.example.server;

import java.lang.management.ManagementFactory;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

public class ServerMain {

    private static final int DEFAULT_NO_THREADS = 10;
    private static final String SYSTEM_NAME = "System";

    public static void main(String[] args) throws MalformedObjectNameException, InterruptedException,
        InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException {

        //Get the MBean server
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

        //register the MBean
        ServerJMX mBean = new ServerJMX(DEFAULT_NO_THREADS, SYSTEM_NAME);
        ObjectName name = new ObjectName("com.example:type=ServerJMX");
        mbs.registerMBean(mBean, name);

        do{
            Thread.sleep(3000);
            System.out.println("Thread Count = " + mBean.getThreadCount() + " :: System Name = " + mBean.getSystemName());
        }while(mBean.isRunning());

        System.out.println("Server JMX stopped");
    }
}