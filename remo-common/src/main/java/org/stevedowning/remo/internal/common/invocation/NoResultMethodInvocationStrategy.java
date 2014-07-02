package org.stevedowning.remo.internal.common.invocation;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutionException;

import org.stevedowning.commons.idyll.idfactory.IdFactory;
import org.stevedowning.remo.internal.common.request.Request;
import org.stevedowning.remo.internal.common.serial.SerializationManager;
import org.stevedowning.remo.internal.common.service.ServiceContext;
import org.stevedowning.remo.internal.common.service.ServiceMethod;

public class NoResultMethodInvocationStrategy implements MethodInvocationStrategy {
    public boolean canHandle(Method method) {
        boolean returnsVoid = method.getReturnType().equals(void.class);
        boolean hasNoExceptions = method.getExceptionTypes().length == 0;
        boolean hasSerializableArgs = areArgsSerializable(method);
        return returnsVoid && hasNoExceptions && hasSerializableArgs;
    }

    @Override
    public Object getVal(IdFactory idFactory, RequestHandler requestHandler,
            SerializationManager serializationManager,
            ServiceContext serviceContext, Method method, Object[] args)
            throws IOException, InterruptedException, ExecutionException {
        Request request = createRequest(
                idFactory, serializationManager, serviceContext, method, args);
        // Don't wait around for a return value. Just fire and forget the request.
        requestHandler.submitRequest(request);
        return null;
    }

    @Override
    public Object invokeServiceMethod(ServiceMethod method, Object handler, Object[] args)
            throws Exception {
        return method.invoke(handler, args);
    }
}