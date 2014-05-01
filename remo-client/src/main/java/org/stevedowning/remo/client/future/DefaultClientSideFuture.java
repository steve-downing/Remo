package org.stevedowning.remo.client.future;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.stevedowning.remo.common.responsehandlers.Callback;
import org.stevedowning.remo.common.responsehandlers.Future;

public class DefaultClientSideFuture<T> implements Future<T> {

    public T get() throws InterruptedException, ExecutionException {
        // TODO Auto-generated method stub
        return null;
    }

    public T get(long timeout, TimeUnit unit) throws InterruptedException,
            ExecutionException {
        // TODO Auto-generated method stub
        return null;
    }

    public Future<T> addCallback(Callback<T> callback) {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean isDone() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean cancel() {
        // TODO Auto-generated method stub
        return false;
    }

}
