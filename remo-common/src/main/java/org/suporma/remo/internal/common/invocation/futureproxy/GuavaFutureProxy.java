package org.suporma.remo.internal.common.invocation.futureproxy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.Executor;

import org.suporma.remo.future.SameThreadExecutor;
import org.suporma.remo.internal.common.CancellationAction;

public class GuavaFutureProxy implements FutureProxy {
    private final Object guavaFuture;
    
    public GuavaFutureProxy() throws NoSuchMethodException, SecurityException,
            ClassNotFoundException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException {
        Method createMethod = getFutureClass().getDeclaredMethod("create");
        createMethod.setAccessible(true);
        this.guavaFuture = createMethod.invoke(null);
    }
    
    public GuavaFutureProxy(Object guavaFuture) {
        this.guavaFuture = guavaFuture;
    }

    public void set(Object val) throws Exception {
        Method setMethod = getFutureClass().getMethod("set", Object.class);
        setMethod.invoke(guavaFuture, val);
    }

    public void setException(Object ex) throws Exception {
        Method setMethod = getFutureClass().getMethod("setException", Throwable.class);
        setMethod.invoke(guavaFuture, ex);
    }

    public Object get() throws Exception {
        Method getMethod = guavaFuture.getClass().getDeclaredMethod("get");
        getMethod.setAccessible(true);
        return getMethod.invoke(guavaFuture);
    }
    
    public void addCancellationAction(CancellationAction cancellationAction) throws Exception {
        Method addListenerMethod =
                getFutureClass().getMethod("addListener", Runnable.class, Executor.class);
        addListenerMethod.invoke(guavaFuture,
                new CancellationRunnable(cancellationAction), new SameThreadExecutor());
    }

    private static Class<?> getFutureClass() throws ClassNotFoundException {
        return getClassLoader().loadClass("com.google.common.util.concurrent.SettableFuture");
    }
    
    private static ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    public Object getBackingFuture() { return guavaFuture; }
    
    private class CancellationRunnable implements Runnable {
        private final CancellationAction cancellationAction;

        public CancellationRunnable(CancellationAction cancellationAction) {
            this.cancellationAction = cancellationAction;
        }

        public void run() {
            try {
                Method cancelledMethod = getFutureClass().getMethod("isCancelled");
                Method interruptedMethod =
                        getFutureClass().getSuperclass().getDeclaredMethod("wasInterrupted");
                interruptedMethod.setAccessible(true);
                boolean cancelled = (boolean)cancelledMethod.invoke(guavaFuture);
                boolean interrupted = (boolean)interruptedMethod.invoke(guavaFuture);
                if (cancelled) {
                    cancellationAction.run(interrupted);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                // TODO: Log this error.
            }
        }
    }
}
