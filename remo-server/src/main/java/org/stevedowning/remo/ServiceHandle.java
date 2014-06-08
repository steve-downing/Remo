package org.stevedowning.remo;

public interface ServiceHandle {
    public void safeShutdown();
    public boolean isRunning();
}
