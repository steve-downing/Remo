package org.stevedowning.remo.internal.common.invocation;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.stevedowning.commons.idyll.idfactory.IdFactory;
import org.stevedowning.remo.internal.common.request.Request;
import org.stevedowning.remo.internal.common.response.Response;
import org.stevedowning.remo.internal.common.serial.SerializationManager;
import org.stevedowning.remo.internal.common.service.ServiceContext;
import org.stevedowning.remo.internal.common.service.ServiceMethod;

public class FutureMethodInvocationStrategy implements MethodInvocationStrategy {
    public boolean canHandle(Method method) {
        Class<?> returnType = method.getReturnType();
        // We can handle this method if it returns a Java Future, a Remo Future, or anything in
        // between.
        boolean returnsFuture = java.util.concurrent.Future.class.isAssignableFrom(returnType) &&
                returnType.isAssignableFrom(org.stevedowning.remo.Future.class);
        return returnsFuture && areArgsSerializable(method);
    }

    public Object getVal(IdFactory idFactory, RequestHandler requestHandler,
            SerializationManager serializationManager,
            ServiceContext serviceContext, Method method, Object[] args)
            throws IOException, InterruptedException, ExecutionException {
        Request request = createRequest(
                idFactory, serializationManager, serviceContext, method, args);
        // TODO: Cancel the request on the server if the Future gets a cancel() request.
        return requestHandler.submitRequest(request).transform((Response result) -> {
            return serializationManager.deserialize(result.getSerializedResult());
        });
    }

    @Override
    public Object invokeServiceMethod(ServiceMethod method, Object handler,
            Object[] args) throws Exception {
        return ((Future<?>)method.invoke(handler, args)).get();
    }
}
