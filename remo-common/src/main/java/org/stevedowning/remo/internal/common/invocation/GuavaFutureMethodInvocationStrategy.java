package org.stevedowning.remo.internal.common.invocation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.stevedowning.commons.idyll.idfactory.IdFactory;
import org.stevedowning.remo.Result;
import org.stevedowning.remo.internal.common.request.Request;
import org.stevedowning.remo.internal.common.response.Response;
import org.stevedowning.remo.internal.common.serial.SerializationManager;
import org.stevedowning.remo.internal.common.service.ServiceContext;
import org.stevedowning.remo.internal.common.service.ServiceMethod;

public class GuavaFutureMethodInvocationStrategy implements MethodInvocationStrategy {
    public boolean canHandle(Method method) {
        Class<?> returnType = method.getReturnType();
        return returnType.getName().equals("com.google.common.util.concurrent.ListenableFuture");
    }

    public Object handleClientInvocation(IdFactory idFactory, RequestHandler requestHandler,
            SerializationManager serializationManager,
            ServiceContext serviceContext, Method method, Object[] args)
            throws Exception {
        Request request = createRequest(
                idFactory, serializationManager, serviceContext, method, args);
        // TODO: Cancel the request on the server if the Future gets a cancel() request.
        //       We may have to do this by wrapping the Guava Future in a proxy class.
        final Object future = getFuture();
        requestHandler.submitRequest(request).addCallback((Result<Response> result) -> {
            try {
                Object val = serializationManager.deserialize(result.get().getSerializedResult());
                if (result.isSuccess()) {
                    setFutureVal(future, val);
                } else {
                    setFutureException(future, val);
                }
            } catch (Exception ex) {
                setFutureException(future, ex);
            }
        });
        return future;
    }
    
    private void setFutureVal(Object /* SettableFuture */ future, Object val)
            throws NoSuchMethodException, SecurityException, ClassNotFoundException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Method setMethod = getFutureClass().getDeclaredMethod("set", Object.class);
        setMethod.setAccessible(true);
        setMethod.invoke(future, val);
    }

    private void setFutureException(Object /* SettableFuture */ future, Object val) {
        try {
            Method setMethod = getFutureClass().getDeclaredMethod("setException", Throwable.class);
            setMethod.setAccessible(true);
            setMethod.invoke(future,  val);
        } catch (Exception ex) {
            // TODO: Log an error somewhere.
        }
    }

    private Object /* SettableFuture */ getFuture() throws ClassNotFoundException,
            NoSuchMethodException, SecurityException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        Method createMethod = getFutureClass().getDeclaredMethod("create");
        createMethod.setAccessible(true);
        return createMethod.invoke(null);
    }

    public Object invokeServiceMethod(ServiceMethod method, Object handler,
            Object[] args) throws Exception {
        Object listenableFuture = method.invoke(handler, args);
        Method getMethod = listenableFuture.getClass().getMethod("get");
        getMethod.setAccessible(true);
        return getMethod.invoke(listenableFuture);
    }
    
    private Class<?> getFutureClass() throws ClassNotFoundException {
        return Thread.currentThread().getContextClassLoader().loadClass(
                "com.google.common.util.concurrent.SettableFuture");
    }
}
