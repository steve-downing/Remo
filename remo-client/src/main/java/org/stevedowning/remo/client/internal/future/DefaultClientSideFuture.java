package org.stevedowning.remo.client.internal.future;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.stevedowning.remo.common.responsehandlers.Callback;
import org.stevedowning.remo.common.responsehandlers.CancelOptions;
import org.stevedowning.remo.common.responsehandlers.CancelResult;
import org.stevedowning.remo.common.responsehandlers.Future;

public class DefaultClientSideFuture<T> implements Future<T> {
    private volatile boolean isDone;
    private volatile InterruptedException interruptedException;
    private volatile ExecutionException executionException;
    private volatile IOException ioException;
    private volatile T val;
    private volatile boolean isError;
    private final CountDownLatch doneLatch;

    private final Collection<Callback<T>> callbacks;
    private final Collection<Runnable> cancellationActions;

    public DefaultClientSideFuture() {
        isDone = false;
        isError = false;
        interruptedException = null;
        executionException = null;
        val = null;
        callbacks = new LinkedList<Callback<T>>();
        cancellationActions = new LinkedList<Runnable>();
        doneLatch = new CountDownLatch(1);
    }
    
    public DefaultClientSideFuture<T> addCancellationAction(Runnable action) {
        this.cancellationActions.add(action);
        return this;
    }

    public boolean cancel() {
        if (isDone) return false;
        setException(new InterruptedException());
        // TODO: Should the cancellation actions happen asynchronously?
        for (Runnable cancellationAction : cancellationActions) {
            cancellationAction.run();
        }
        return true;
    }
    
    public boolean isError() { return isError; }

    public synchronized DefaultClientSideFuture<T> addCallback(Callback<T> callback) {
        if (isDone) {
            invokeCallback(callback);
        } else {
            callbacks.add(callback);
        }
        return this;
    }

    public T get(long timeout, TimeUnit unit)
            throws InterruptedException, ExecutionException, IOException {
        if (doneLatch.await(timeout, unit)) {
            return get();
        } else {
            throw new InterruptedException();
        }
    }

    public T get() throws InterruptedException, ExecutionException, IOException {
        doneLatch.await();
        if (executionException != null) {
            throw executionException;
        } else if (interruptedException != null) {
            throw interruptedException;
        } else if (ioException != null) {
            throw ioException;
        } else {
            return val;
        }
    }

    public boolean isDone() { return isDone; }

    public synchronized boolean setVal(T val) {
        if (this.isDone) return false;
        this.val = val;
        harden();
        return true;
    }

    public synchronized boolean setException(InterruptedException ex) {
        if (this.isDone) return false;
        this.interruptedException = ex;
        this.isError = true;
        harden();
        return true;
    }

    public synchronized boolean setException(IOException ex) {
        if (this.isDone) return false;
        this.ioException = ex;
        this.isError = true;
        harden();
        return true;
    }

    public synchronized boolean setException(ExecutionException ex) {
        if (this.isDone) return false;
        this.executionException = ex;
        this.isError = true;
        harden();
        return true;
    }
    
    /**
     * Lock down this future. It's already received its result. It's no longer mutable.
     */
    private synchronized void harden() {
        this.isDone = true;
        this.doneLatch.countDown();
        invokeCallbacks();
    }

    private void invokeCallbacks() {
        for (Callback<T> callback : callbacks) {
            invokeCallback(callback);
        }
    }

    private void invokeCallback(Callback<T> callback) {
        callback.handleResponse(this);
    }

    public Future<CancelResult> cancel(CancelOptions options) {
        // TODO: Fill this in. This should cancel the request on the client, and optionally on the
        //       service.
        // TODO: Should this return and object that keeps track of the various stages of
        //       cancellation success and failure?
        cancel();
        DefaultClientSideFuture<CancelResult> future = new DefaultClientSideFuture<CancelResult>();
        future.setVal(CancelResult.CANCEL_ON_CLIENT_ONLY);
        return future;
    }
}
