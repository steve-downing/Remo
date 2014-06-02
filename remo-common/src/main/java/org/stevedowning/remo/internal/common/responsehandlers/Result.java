package org.stevedowning.remo.internal.common.responsehandlers;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public interface Result<T> {
    public T get() throws InterruptedException, ExecutionException, IOException;
    public boolean isError();
}
