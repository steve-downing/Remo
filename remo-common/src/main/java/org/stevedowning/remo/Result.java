package org.stevedowning.remo;

import java.util.concurrent.ExecutionException;

public interface Result<T> {
    public T get() throws InterruptedException, ExecutionException;
    public boolean isSuccess();
}
