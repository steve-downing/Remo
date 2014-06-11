package org.stevedowning.remo;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.stevedowning.remo.internal.common.future.TransformedFuture;

public interface Future<T> extends Result<T> {
    public T get(long timeout, TimeUnit unit)
            throws InterruptedException, ExecutionException, IOException;
    public Future<T> addCallback(Callback<T> callback);
    public Future<T> addCancellationAction(Runnable action);
    public boolean isDone();
    public boolean isError();
    public boolean isCancelled();
    public boolean cancel();

    /**
     * This returns a Future that, upon completion of this Future, transforms the result into a new
     * result as per the provided function.
     * @param transformFunction The function used to transform this Future's result into another.
     * @return A new Future that promises the transformed result.
     */
    default public <U> Future<U> transform(final ThrowingFunction<T, U> transformFunction) {
        return new TransformedFuture<>(this, transformFunction);
    }
}
