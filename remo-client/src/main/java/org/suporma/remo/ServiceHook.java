package org.suporma.remo;

import java.util.concurrent.TimeUnit;

public interface ServiceHook<T> {
    public T getService();
    public void runBatch(Runnable batch); // TODO: Use ServiceRunnable.
    public void setDefaultTimeout(long duration, TimeUnit timeUnit);
    // TODO: public boolean isServiceAvailable() throws IOException;
}
