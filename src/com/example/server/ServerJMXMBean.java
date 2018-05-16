package com.example.server;

public interface ServerJMXMBean {

    public void setThreadCount(int noOfThreads);
    public int getThreadCount();

    public void setSystemName(String systemName);
    public String getSystemName();

    public boolean isRunning();
    public void stop();
}
