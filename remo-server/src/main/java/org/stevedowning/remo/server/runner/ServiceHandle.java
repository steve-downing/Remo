package org.stevedowning.remo.server.runner;

public interface ServiceHandle {
    public void safeShutdown();
    public boolean isRunning();
}
