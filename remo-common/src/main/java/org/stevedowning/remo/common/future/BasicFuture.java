package org.stevedowning.remo.common.future;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
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

    private final Queue<Callback<T>> callbacks;
    private final Queue<Runnable> cancellationActions;

    // TODO: Allow the client to provide an optional executor service that runs callbacks.
    public BasicFuture() {
        isDone = false;
        isCancelled = false;
        isError = false;
        interruptedException = null;
        executionException = null;
        val = null;
        callbacks = new ConcurrentLinkedQueue<Callback<T>>();
        cancellationActions = new ConcurrentLinkedQueue<Runnable>();
        doneLatch = new CountDownLatch(1);
    }

    public boolean cancel() {
        // This is to avoid the potentially blocking call to setException.
        if (isDone) return false;

        isCancelled = true;
        if (setException(new InterruptedException())) {
            return true;
        } else {
            return false;
        }
    }
    
    public boolean isError() { return isError; }

    public BasicFuture<T> addCallback(Callback<T> callback) {
        if (callback == null) return this;
        if (isDone) {
            invokeCallback(callback);
        } else {
            callbacks.offer(callback);
            // Clear this callback out if we've hit the race condition that leaves this callback in
            // the queue after we think we're done pumping them all out.
            if (isDone && callbacks.remove(callback)) {
                invokeCallback(callback);
            }
        }
        return this;
    }
    
    public BasicFuture<T> addCancellationAction(Runnable action) {
        if (action == null) return this;
        if (isCancelled) {
            action.run();
        } else if (!isDone) {
            this.cancellationActions.offer(action);
            // Clear this callback out if we've hit the race condition that leaves this callback in
            // the queue after we think we're done pumping them all out.
            if (isCancelled && cancellationActions.remove(action)) {
                action.run();
            }
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
        if (isDone) return false;
        this.val = val;
        harden();
        return true;
    }

    public synchronized boolean setException(InterruptedException ex) {
        if (isDone) return false;
        interruptedException = ex;
        isError = true;
        harden();
        return true;
    }

    public synchronized boolean setException(IOException ex) {
        if (isDone) return false;
        ioException = ex;
        isError = true;
        harden();
        return true;
    }

    public synchronized boolean setException(ExecutionException ex) {
        if (isDone) return false;
        executionException = ex;
        isError = true;
        harden();
        return true;
    }
    
    /**
     * Lock down this future. It's already received its result. It's no longer mutable.
     */
    private synchronized void harden() {
        isDone = true;
        doneLatch.countDown();
        invokeCallbacks();
    }

    private void invokeCallbacks() {
        if (isCancelled) {
            for (Runnable action; (action = cancellationActions.poll()) != null;) {
                action.run();
            }
        } else {
            cancellationActions.clear();
        }
        for (Callback<T> callback; (callback = callbacks.poll()) != null;) {
            invokeCallback(callback);
        }
    }

    private void invokeCallback(Callback<T> callback) {
        callback.handleResponse(this);
    }
}
