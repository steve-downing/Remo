package org.stevedowning.remo.internal.common.invocation;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutionException;

import org.stevedowning.commons.idyll.idfactory.IdFactory;
import org.stevedowning.remo.internal.common.request.Request;
import org.stevedowning.remo.internal.common.serial.SerializationManager;
import org.stevedowning.remo.internal.common.service.ServiceContext;
import org.stevedowning.remo.internal.common.service.ServiceMethod;

public class SimpleMethodInvocationStrategy implements MethodInvocationStrategy {
    public boolean canHandle(Method method) {
        return areArgsAndReturnTypeSerializable(method);
    }

    public Object getVal(IdFactory idFactory, RequestHandler requestHandler,
            SerializationManager serializationManager,
            ServiceContext serviceContext, Method method, Object[] args)
                    throws IOException, InterruptedException, ExecutionException {
        Request request = createRequest(
                idFactory, serializationManager, serviceContext, method, args);
        try {
            return serializationManager.deserialize(
                    requestHandler.submitRequest(request).get().getSerializedResult());
        } catch (ClassNotFoundException ex) {
            throw new ExecutionException(ex);
        }
    }

    public Object invokeServiceMethod(ServiceMethod method, Object handler, Object[] args)
            throws Exception {
        return method.invoke(handler, args);
    }
}
