package org.stevedowning.remo.internal.common.invocation.futureproxy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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
        Method setMethod = getFutureClass().getDeclaredMethod("set", Object.class);
        setMethod.setAccessible(true);
        setMethod.invoke(guavaFuture, val);
    }

    public void setException(Object ex) throws Exception {
        Method setMethod = getFutureClass().getDeclaredMethod("setException", Throwable.class);
        setMethod.setAccessible(true);
        setMethod.invoke(guavaFuture, ex);
    }

    public Object get() throws Exception {
        Method getMethod = guavaFuture.getClass().getMethod("get");
        getMethod.setAccessible(true);
        return getMethod.invoke(guavaFuture);
    }

    private static Class<?> getFutureClass() throws ClassNotFoundException {
        return Thread.currentThread().getContextClassLoader().loadClass(
                "com.google.common.util.concurrent.SettableFuture");
    }

    public Object getBackingFuture() { return guavaFuture; }
}
