package org.stevedowning.remo.client;

import java.util.concurrent.TimeUnit;

public interface ServiceHook<T> {
    public T getService();
    public void runBatch(Runnable batch);
    public void setDefaultTimeout(long duration, TimeUnit timeUnit);
}