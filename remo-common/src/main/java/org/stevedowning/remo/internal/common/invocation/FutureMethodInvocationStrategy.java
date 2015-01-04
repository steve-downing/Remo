package org.stevedowning.remo.internal.common.invocation;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.stevedowning.remo.internal.common.request.CancellationRequest;
import org.stevedowning.remo.internal.common.request.InvocationRequest;
import org.stevedowning.remo.internal.common.response.Response;
import org.stevedowning.remo.internal.common.serial.SerializationManager;
import org.stevedowning.remo.internal.common.service.ServiceContext;
import org.stevedowning.remo.internal.common.service.ServiceMethod;
import org.suporma.idyll.util.IdFactory;

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
        InvocationRequest request = createRequest(
                idFactory, serializationManager, serviceContext, method, args);
        return requestHandler.submitRequest(request).transform((Response response) ->
            getOrThrowFromResponse(serializationManager, response)
        ).addCancellationAction((boolean mayInterruptIfRunning) -> {
            if (mayInterruptIfRunning) {
                requestHandler.submitRequest(new CancellationRequest(
                        idFactory.generateId(), request.getId()));
            }
        });
    }

    @Override
    public Object invokeServiceMethod(ServiceMethod method, Object handler,
            Object[] args) throws Exception {
        return ((Future<?>)method.invoke(handler, args)).get();
    }
}
