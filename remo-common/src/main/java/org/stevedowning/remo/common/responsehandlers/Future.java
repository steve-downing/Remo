package org.stevedowning.remo.common.responsehandlers;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public interface Future<T> extends Response<T> {
    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException;
    public Future<T> addCallback(Callback<T> callback);
    public boolean isDone();
    public boolean cancel();
}
