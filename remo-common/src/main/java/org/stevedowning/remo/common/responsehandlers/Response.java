package org.stevedowning.remo.common.responsehandlers;

import java.util.concurrent.ExecutionException;

public interface Response<T> {
    public T get() throws InterruptedException, ExecutionException;
    public boolean isError();
}
