package org.stevedowning.remo.internal.server.service;

public class ThreadHandle {
    private final Thread thread;
    private volatile boolean isExecuting = false;
    private volatile boolean wasInterrupted = false;
    
    public ThreadHandle(Thread thread) {
        this.thread = thread;
    }
    
    public synchronized void setExecuting(boolean isExecuting) { this.isExecuting = isExecuting; }
    public boolean isExecuting() { return isExecuting; }
    public boolean wasInterrupted() { return wasInterrupted; }
    
    public synchronized void interrupt() {
        if (isExecuting) {
            isExecuting = false;
            thread.interrupt();
            wasInterrupted = true;
        }
    }
}
