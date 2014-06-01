package org.stevedowning.remo.common.future;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.stevedowning.remo.common.responsehandlers.Callback;

public class BasicFuture<T> implements Future<T> {
    private volatile boolean isDone, isCancelled, isError;
    private volatile InterruptedException interruptedException;
    private volatile ExecutionException executionException;
    private volatile IOException ioException;
    private volatile T val;
    private final CountDownLatch doneLatch;

    private final Collection<Callback<T>> callbacks;
    private final Collection<Runnable> cancellationActions;

    public BasicFuture() {
        isDone = false;
        isCancelled = false;
        isError = false;
        interruptedException = null;
        executionException = null;
        val = null;
        callbacks = new LinkedList<Callback<T>>();
        cancellationActions = new LinkedList<Runnable>();
        doneLatch = new CountDownLatch(1);
    }
    
    public synchronized BasicFuture<T> addCancellationAction(Runnable action) {
        if (isCancelled) {
            action.run();
        } else {
            this.cancellationActions.add(action);
        }
        return this;
    }

    public boolean cancel() {
        if (setException(new InterruptedException())) {
            isCancelled = true;
            // TODO: Should the cancellation actions happen asynchronously?
            for (Runnable cancellationAction : cancellationActions) {
                cancellationAction.run();
            }
            return true;
        } else {
            return false;
        }
    }
    
    public boolean isError() { return isError; }

    public synchronized BasicFuture<T> addCallback(Callback<T> callback) {
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
}
