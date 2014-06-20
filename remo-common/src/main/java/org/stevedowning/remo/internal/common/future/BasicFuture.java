package org.stevedowning.remo.internal.common.future;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import org.stevedowning.remo.Callback;
import org.stevedowning.remo.Future;
import org.stevedowning.remo.Result;

public class BasicFuture<T> implements Future<T> {
    private volatile boolean isError, isCancelled, isSuccess;
    private final ErrorContainer error;
    private volatile T val;
    private final CountDownLatch doneLatch;
    private final Executor executor;

    private final Queue<Callback<T>> callbacks;

    public BasicFuture(Executor executor) {
        if (executor == null) throw new IllegalArgumentException();
        isSuccess = false;
        isCancelled = false;
        isError = false;
        error = new ErrorContainer();
        val = null;
        callbacks = new ConcurrentLinkedQueue<Callback<T>>();
        doneLatch = new CountDownLatch(1);
        this.executor = executor;
    }
    
    public BasicFuture() {
        this(new SameThreadExecutor());
    }
    
    public BasicFuture<T> addCallback(Callback<T> callback) {
        if (callback == null) return this;
        if (isDone()) {
            invokeCallback(callback);
        } else {
            callbacks.offer(callback);
            // Clear this callback out if we've hit the race condition that leaves it in
            // the queue after we think we're done pumping everything out.
            if (isDone() && callbacks.remove(callback)) {
                invokeCallback(callback);
            }
        }
        return this;
    }
    
    public BasicFuture<T> addCancellationAction(Runnable action) {
        if (action == null) return this;
        addCallback((Result<T> result) -> { if (isCancelled) action.run(); });
        return this;
    }
    
    // TODO: public BasicFuture<T> addBlockingGetAction(Runnable action)
    // This will run an action at any time that a get() is called while !isDone.
    // Effectively, this allows other objects to be notified when a thread is
    // blocking on this result.

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
        error.possiblyThrow();
        return val;
    }

    public boolean isDone() { return isSuccess || isCancelled || isError; }
    public boolean isError() { return isError; }
    public boolean isCancelled() { return isCancelled; }
    public boolean isSuccess() { return isSuccess; }

    public boolean setVal(T val) {
        if (isDone()) return false;
        synchronized (this) {
            if (isDone()) return false;
            this.val = val;
            isSuccess = true;
        }
        harden();
        return true;
    }

    public boolean cancel() {
        if (isDone()) return false;
        synchronized (this) {
            if (isDone()) return false;
            error.setError(new InterruptedException());
            isCancelled = true;
        }
        harden();
        return true;
    }

    public boolean setException(Exception ex) {
        if (isDone()) return false;
        synchronized (this) {
            if (isDone()) return false;
            error.setError(ex);
            isError = true;
        }
        harden();
        return true;
    }
    
    /**
     * Lock down this future. It's already received its result. It's no longer mutable.
     */
    private void harden() {
        doneLatch.countDown();
        invokeCallbacks();
    }

    private void invokeCallbacks() {
        for (Callback<T> callback; (callback = callbacks.poll()) != null;) {
            invokeCallback(callback);
        }
    }

    private void invokeCallback(Callback<T> callback) {
        executor.execute(() -> callback.handleResult(this));
    }
}
