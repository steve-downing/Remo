package org.stevedowning.remo.internal.common.invocation;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutionException;

import org.stevedowning.commons.idyll.idfactory.IdFactory;
import org.stevedowning.remo.Future;
import org.stevedowning.remo.internal.common.request.Request;
import org.stevedowning.remo.internal.common.serial.SerializationManager;
import org.stevedowning.remo.internal.common.service.ServiceContext;

public class RemoFutureMethodInvocationStrategy implements MethodInvocationStrategy {
    public boolean canHandle(Method method) {
        return method.getReturnType().equals(Future.class);
    }

    public Object getVal(IdFactory idFactory, RequestHandler requestHandler,
            SerializationManager serializationManager,
            ServiceContext serviceContext, Method method, Object[] args)
            throws IOException, InterruptedException, ExecutionException {
        Request request = createRequest(
                idFactory, serializationManager, serviceContext, method, args);
        // TODO: Cancel the request on the server if the Future gets a cancel() request.
        return requestHandler.submitRequest(request);
    }

    @Override
    public Object invokeServiceMethod(Method method, Object handler,
            Object[] args) throws Exception {
        // TODO: Handle InvocationTargetExceptions?
        return ((Future<?>)method.invoke(handler, args)).get();
    }
}
