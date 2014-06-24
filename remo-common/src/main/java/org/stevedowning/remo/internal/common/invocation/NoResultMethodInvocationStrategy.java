package org.stevedowning.remo.internal.common.invocation;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutionException;

import org.stevedowning.commons.idyll.idfactory.IdFactory;
import org.stevedowning.remo.internal.common.request.Request;
import org.stevedowning.remo.internal.common.serial.SerializationManager;
import org.stevedowning.remo.internal.common.service.ServiceContext;

public class NoResultMethodInvocationStrategy implements MethodInvocationStrategy {
    public boolean canHandle(Method method) {
       return method.getReturnType().equals(Void.class) && method.getExceptionTypes().length == 0 &&
               areArgsSerializable(method);
    }

    @Override
    public Object getVal(IdFactory idFactory, RequestHandler requestHandler,
            SerializationManager serializationManager,
            ServiceContext serviceContext, Method method, Object[] args)
            throws IOException, InterruptedException, ExecutionException {
        Request request = createRequest(
                idFactory, serializationManager, serviceContext, method, args);
        requestHandler.submitRequest(request);
        return null;
    }

    @Override
    public Object invokeServiceMethod(Method method, Object handler, Object[] args)
            throws Exception {
        return method.invoke(handler, args);
    }
}
