package org.stevedowning.remo.internal.common.future;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.stevedowning.remo.Callback;
import org.stevedowning.remo.Future;
import org.stevedowning.remo.internal.common.CancellationAction;

public class PresetFuture<T> implements Future<T> {
    private final T val;
    private final ErrorContainer error;
    private final ExecutorService executorService;

    public PresetFuture(T val) {
        this(null, val);
    }
    
    public PresetFuture(ExecutorService executorService, T val) {
        this.executorService = executorService;
        this.val = val;
        this.error = new ErrorContainer();
    }
    
    public PresetFuture(Callable<T> valFactory) {
        this(null, valFactory);
    }
    
    public PresetFuture(ExecutorService executorService, Callable<T> valFactory) {
        this.executorService = executorService;
        this.error = new ErrorContainer();
        T val;
        try {
            val = valFactory.call();
        } catch (Exception ex) {
            val = null;
            error.setError(ex);
        }
        this.val = val;
    }

    public T get() throws InterruptedException, ExecutionException {
        error.possiblyThrow();
        return val;
    }
    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException {
        return get();
    }

    public PresetFuture<T> addCallback(Callback<T> callback) {
        if (executorService == null) {
            callback.handleResult(this);
        } else {
            executorService.submit(() -> callback.handleResult(this));
        }
        return this;
    }
    public PresetFuture<T> addCancellationAction(CancellationAction action) { return this; }

    public boolean isSuccess() { return !isError(); }
    public boolean isError() { return error.hasError(); }
    public boolean isDone() { return true; }
    public boolean isCancelled() { return false; }
    public boolean cancel(boolean mayInterruptIfRunning) { return false; }
}
