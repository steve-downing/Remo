package org.stevedowning.remo;

import java.util.concurrent.TimeUnit;

public interface ServiceHook<T> {
    public T getService();
    public void runBatch(Runnable batch); // TODO: Use ServiceRunnable.
    public void setDefaultTimeout(long duration, TimeUnit timeUnit);
    // TODO: Convenience method for running potentially blocking calls in another thread.
    // public void do(ServiceRunnable runnable);
}
