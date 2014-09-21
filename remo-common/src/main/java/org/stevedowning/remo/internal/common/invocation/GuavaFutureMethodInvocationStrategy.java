package org.stevedowning.remo.internal.common.invocation;

import java.lang.reflect.Method;

import org.stevedowning.commons.idyll.idfactory.IdFactory;
import org.stevedowning.remo.Result;
import org.stevedowning.remo.internal.common.invocation.futureproxy.FutureProxy;
import org.stevedowning.remo.internal.common.invocation.futureproxy.GuavaFutureProxy;
import org.stevedowning.remo.internal.common.request.InvocationRequest;
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
        InvocationRequest request = createRequest(
                idFactory, serializationManager, serviceContext, method, args);
        // TODO: Cancel the request on the server if the Future gets a cancel() request.
        final FutureProxy future = new GuavaFutureProxy();
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
