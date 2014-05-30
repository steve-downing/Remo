package org.stevedowning.remo.common.responsehandlers;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.stevedowning.remo.common.future.BasicFuture;

public interface Future<T> extends Response<T> {
    public T get(long timeout, TimeUnit unit)
            throws InterruptedException, ExecutionException, IOException;
    public Future<T> addCallback(Callback<T> callback);
    public Future<T> addCancellationAction(Runnable action);
    public boolean isDone();
    public boolean cancel();

    /**
     * This returns a Future that, upon completion of this Future, transforms the result into a new
     * result as per the provided function.
     * @param transformFunction The function used to transform this Future's result into another.
     * @return A new Future that promises the transformed result.
     */
    default public <U> Future<U> transform(final Function<T, U> transformFunction) {
        BasicFuture<U> future = new BasicFuture<U>();
        addCallback((Response<T> response) -> {
            boolean gotPreVal = false;
            T preVal = null;
            try {
                preVal = response.get();
                gotPreVal = true;
            } catch (InterruptedException ex) {
                future.setException(ex);
            } catch (ExecutionException ex) {
                future.setException(ex);
            } catch (IOException ex) {
                future.setException(ex);
            }
            if (gotPreVal) {
                try {
                    U postVal = transformFunction.apply(preVal);
                    future.setVal(postVal);
                } catch (Exception ex) {
                    // This should catch transformation errors.
                    future.setException(new ExecutionException(ex));
                }
            }
        });
        addCancellationAction(() -> future.cancel());
        return future;
    }
}
