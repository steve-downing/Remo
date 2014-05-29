package org.stevedowning.remo.common.responsehandlers;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public interface Future<T> extends Response<T> {
    public T get(long timeout, TimeUnit unit)
            throws InterruptedException, ExecutionException, IOException;
    public Future<T> addCallback(Callback<T> callback);
    public boolean isDone();
    public boolean cancel();
}
