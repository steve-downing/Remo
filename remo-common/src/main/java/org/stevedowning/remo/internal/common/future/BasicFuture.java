package org.stevedowning.remo.internal.common.future;

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
    private volatile boolean isError, isCancelled, isSuccess, cancelMayInterruptIfRunning,
        hasWaitingClient;
    private final ErrorContainer error;
    private volatile T val;
    private final CountDownLatch doneLatch;
    private final Executor executor;

    private final Queue<Callback<T>> callbacks;
    private final Queue<Runnable> waitingClientActions;

    public BasicFuture(Executor executor) {
        if (executor == null) throw new IllegalArgumentException();
        isSuccess = false;
        isCancelled = false;
        isError = false;
        cancelMayInterruptIfRunning = false;
        hasWaitingClient = false;
        error = new ErrorContainer();
        val = null;
        callbacks = new ConcurrentLinkedQueue<Callback<T>>();
        waitingClientActions = new ConcurrentLinkedQueue<Runnable>();
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
    
    /**
     * This function adds an action that may interrupt execution when this Future is cancelled.
     * It'll only fire if this is cancelled with the mayInterruptIfRunning flag.
     */
    public BasicFuture<T> addCancellationInterrupt(Runnable action) {
        if (action == null) return this;
        addCallback((Result<T> result) -> {
            if (isCancelled && cancelMayInterruptIfRunning) action.run();
        });
        return this;
    }
    
    /**
     * This function adds an action that fires when a blocking get() is called on this Future.
     * This lets the client know when a thread is actively waiting on this Future's result.
     * If a thread has already called a blocking get() when this method is called, the action will
     * be enqueued for execution. This is true even if the get() has expired or been cancelled.
     * This action will often not fire at all. At most, it will fire once.
     */
    public BasicFuture<T> addBlockingGetAction(Runnable action) {
        if (action == null || isDone()) return this;
        if (hasWaitingClient) {
            executor.execute(action);
        } else {
            waitingClientActions.offer(action);
            if (isDone()) callbacks.remove(action);
        }
        return this;
    }
    
    private void fireBlockingGetActions() {
        for (Runnable action; (action = waitingClientActions.poll()) != null;) {
            if (!isDone()) executor.execute(action);
        }
    }

    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException {
        hasWaitingClient = true;
        fireBlockingGetActions();
        if (doneLatch.await(timeout, unit)) {
            return get();
        } else {
            throw new InterruptedException();
        }
    }

    public T get() throws InterruptedException, ExecutionException {
        hasWaitingClient = true;
        fireBlockingGetActions();
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

    public boolean cancel(boolean mayInterruptIfRunning) {
        if (isDone()) return false;
        synchronized (this) {
            if (isDone()) return false;
            error.setError(new InterruptedException());
            cancelMayInterruptIfRunning = mayInterruptIfRunning;
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
        waitingClientActions.clear();
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
