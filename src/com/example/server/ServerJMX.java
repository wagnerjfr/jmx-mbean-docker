package com.example.server;

import javax.management.AttributeChangeNotification;
import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;

public class ServerJMX extends NotificationBroadcasterSupport implements ServerJMXMBean {

    private long sequenceNumber = 1;
    private int threadCount;
    private String systemName;
    private boolean running = true;

    public ServerJMX(int numThreads, String systemName){
        this.threadCount = numThreads;
        this.systemName = systemName;
    }

    @Override
    public void setThreadCount(int noOfThreads) {
        int oldValue = this.threadCount;
        this.threadCount=noOfThreads;
        Notification n =  new AttributeChangeNotification(this,
                sequenceNumber++, System.currentTimeMillis(), "threadCount changed",
                "threadCount", "int", oldValue, this.threadCount);
        sendNotification(n);
    }

    @Override
    public int getThreadCount() {
        return this.threadCount;
    }

    @Override
    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    @Override
    public String getSystemName() {
        return this.systemName;
    }

    @Override
    public void stop() {
        System.out.println("Client requested to stop..");
        this.running = false;
    }

    @Override
    public boolean isRunning() {
        return running;
    }
}