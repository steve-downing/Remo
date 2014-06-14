package org.stevedowning.remo.internal.common.future;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.stevedowning.remo.Callback;
import org.stevedowning.remo.Future;
import org.stevedowning.remo.Result;

public class BasicFuture<T> implements Future<T> {
    private volatile boolean isError, isCancelled, isSuccess;
    private volatile InterruptedException interruptedException;
    private volatile ExecutionException executionException;
    private volatile IOException ioException;
    private volatile T val;
    private final CountDownLatch doneLatch;
    private volatile ExecutorService executorService;

    private final Queue<Callback<T>> callbacks;

    public BasicFuture(ExecutorService executorService) {
        isSuccess = false;
        isCancelled = false;
        isError = false;
        interruptedException = null;
        executionException = null;
        val = null;
        callbacks = new ConcurrentLinkedQueue<Callback<T>>();
        doneLatch = new CountDownLatch(1);
        this.executorService = executorService;
    }
    
    // TODO: Make a ctor that takes a function that returns a value and handles exceptions.
    
    public BasicFuture() {
        this(null);
    }

    public boolean cancel() {
        // Quick check to avoid a potentially blocking call.
        if (isDone()) return false;
        return setCancelled();
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

    public boolean isDone() { return isSuccess || isCancelled || isError; }
    public boolean isError() { return isError; }
    public boolean isCancelled() { return isCancelled; }
    public boolean isSuccess() { return isSuccess; }

    public synchronized boolean setVal(T val) {
        if (isDone()) return false;
        this.val = val;
        isSuccess = true;
        harden();
        return true;
    }
    
    private synchronized boolean setCancelled() {
        if (isDone()) return false;
        isCancelled = true;
        return setException(new InterruptedException());
    }

    public synchronized boolean setException(InterruptedException ex) {
        if (isDone()) return false;
        interruptedException = ex;
        isError = !isCancelled; // Importantly, cancellation isn't an error state.
        harden();
        return true;
    }

    public synchronized boolean setException(IOException ex) {
        if (isDone()) return false;
        ioException = ex;
        isError = true;
        harden();
        return true;
    }

    public synchronized boolean setException(ExecutionException ex) {
        if (isDone()) return false;
        executionException = ex;
        isError = true;
        harden();
        return true;
    }
    
    /**
     * Lock down this future. It's already received its result. It's no longer mutable.
     */
    private synchronized void harden() {
        doneLatch.countDown();
        invokeCallbacks();
    }

    private void invokeCallbacks() {
        for (Callback<T> callback; (callback = callbacks.poll()) != null;) {
            invokeCallback(callback);
        }
    }

    private void invokeCallback(Callback<T> callback) {
        if (executorService == null) {
            callback.handleResult(this);
        } else {
            // This is safe because, even though our ExecutorService is volatile, nothing can
            // change it from non-null to null.
            executorService.submit(() -> callback.handleResult(this));
        }
    }
}
