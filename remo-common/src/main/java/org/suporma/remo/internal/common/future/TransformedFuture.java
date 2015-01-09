package org.suporma.remo.internal.common.future;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.suporma.remo.Callback;
import org.suporma.remo.Future;
import org.suporma.remo.Result;
import org.suporma.remo.ThrowingFunction;
import org.suporma.remo.future.PresetFuture;
import org.suporma.remo.internal.common.CancellationAction;

public class TransformedFuture<T, U> implements Future<U> {
    private final Future<T> backingFuture;
    private final ThrowingFunction<T, U> transformFunction;
    private volatile Result<U> transformedResult = null;
    private volatile boolean isTransformationError = false;

    public TransformedFuture(Future<T> future, ThrowingFunction<T, U> transformFunction) {
        this.backingFuture = future;
        this.transformFunction = transformFunction;
        
        // Eagerly transform the result.
        future.addCallback((Result<T> result) -> cacheTransformedResult());
    }
    
    private U transformVal(T val) throws ExecutionException {
        if (val == null) return null;
        try {
            return transformFunction.apply(val);
        } catch (Throwable ex) {
            isTransformationError = true;
            throw new ExecutionException(ex);
        }
    }
    
    private synchronized void cacheTransformedResult() {
        if (transformedResult != null) return;
        this.transformedResult = new PresetFuture<U>(() -> {
            T val = backingFuture.get();
            U transformedVal = transformVal(val);
            return transformedVal;
        });
    }

    public U get() throws InterruptedException, ExecutionException {
        backingFuture.get();
        cacheTransformedResult();
        return transformedResult.get();
    }

    public U get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException {
        backingFuture.get(timeout, unit);
        cacheTransformedResult();
        return transformedResult.get();
    }

    public boolean isDone() {
        return backingFuture.isDone() && transformedResult != null;
    }
    public boolean isCancelled() { return backingFuture.isCancelled(); }
    public boolean isError() {
        return isTransformationError() || backingFuture.isError();
    }
    public boolean isSuccess() {
        return backingFuture.isSuccess() && transformedResult != null && !isTransformationError();
    }
    private boolean isTransformationError() {
        return isTransformationError;
    }

    public TransformedFuture<T, U> addCallback(Callback<U> callback) {
        backingFuture.addCallback((Result<T> result) -> {
            cacheTransformedResult();
            callback.handleResult(transformedResult);
        });
        return this;
    }

    public TransformedFuture<T, U> addCancellationAction(CancellationAction action) {
        backingFuture.addCancellationAction(action);
        return this;
    }

    public boolean cancel(boolean mayInterruptIfRunning) {
        return backingFuture.cancel(mayInterruptIfRunning);
    }
}
