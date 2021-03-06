package org.suporma.remo.internal.common.invocation;

import java.lang.reflect.Method;

import org.suporma.idyll.util.IdFactory;
import org.suporma.remo.Result;
import org.suporma.remo.internal.common.invocation.futureproxy.FutureProxy;
import org.suporma.remo.internal.common.invocation.futureproxy.GuavaFutureProxy;
import org.suporma.remo.internal.common.request.CancellationRequest;
import org.suporma.remo.internal.common.request.InvocationRequest;
import org.suporma.remo.internal.common.response.Response;
import org.suporma.remo.internal.common.serial.SerializationManager;
import org.suporma.remo.internal.common.service.ServiceContext;
import org.suporma.remo.internal.common.service.ServiceMethod;

public class GuavaFutureMethodInvocationStrategy implements MethodInvocationStrategy {
    public boolean canHandle(Method method) {
        Class<?> returnType = method.getReturnType();
        return returnType.getName().equals("com.google.common.util.concurrent.ListenableFuture");
    }

    public Object handleClientInvocation(IdFactory idFactory, RequestHandler requestHandler,
            SerializationManager serializationManager,
            ServiceContext serviceContext, Method method, Object[] args)
            throws Exception {
        InvocationRequest request = createRequest(
                idFactory, serializationManager, serviceContext, method, args);
        final FutureProxy future = new GuavaFutureProxy();
        future.addCancellationAction((boolean mayInterruptIfRunning) -> {
            if (mayInterruptIfRunning) {
                requestHandler.submitRequest(new CancellationRequest(
                        idFactory.generateId(), request.getId()));
            }
        });
        requestHandler.submitRequest(request).addCallback((Result<Response> result) -> {
            try {
                Object val = serializationManager.deserialize(result.get().getSerializedResult());
                if (result.isSuccess()) {
                    future.set(val);
                } else {
                    future.setException(val);
                }
            } catch (Exception ex) {
                try {
                    future.setException(ex);
                } catch (Exception e) {
                    // TODO: Log an error somewhere.
                }
            }
        });
        return future.getBackingFuture();
    }

    public Object invokeServiceMethod(ServiceMethod method, Object handler,
            Object[] args) throws Exception {
        FutureProxy futureProxy = new GuavaFutureProxy(method.invoke(handler, args));
        return futureProxy.get();
    }
}
