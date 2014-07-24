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
        boolean returnsFuture = isBetweenInClassHierarchy(
                java.util.concurrent.Future.class, org.stevedowning.remo.Future.class, returnType);
        return returnsFuture && areArgsSerializable(method);
    }

    public Object handleClientInvocation(IdFactory idFactory, RequestHandler requestHandler,
            SerializationManager serializationManager,
            ServiceContext serviceContext, Method method, Object[] args)
            throws IOException, InterruptedException, ExecutionException {
        Request request = createRequest(
                idFactory, serializationManager, serviceContext, method, args);
        // TODO: Cancel the request on the server if the Future gets a cancel() request.
        // TODO: This is pretty similar to how GuavaFutureMethodInvocationStrategy works now.
        //       Try and consolidate them.
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
