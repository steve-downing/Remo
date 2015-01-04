package org.stevedowning.remo.internal.common.invocation;

import java.lang.reflect.Method;

import org.stevedowning.remo.internal.common.request.InvocationRequest;
import org.stevedowning.remo.internal.common.response.Response;
import org.stevedowning.remo.internal.common.serial.SerializationManager;
import org.stevedowning.remo.internal.common.service.ServiceContext;
import org.stevedowning.remo.internal.common.service.ServiceMethod;
import org.suporma.idyll.util.IdFactory;

public class SimpleMethodInvocationStrategy implements MethodInvocationStrategy {
    public boolean canHandle(Method method) {
        return areArgsAndReturnTypeSerializable(method);
    }

    public Object handleClientInvocation(IdFactory idFactory, RequestHandler requestHandler,
            SerializationManager serializationManager,
            ServiceContext serviceContext, Method method, Object[] args) throws Throwable {
        InvocationRequest request = createRequest(
                idFactory, serializationManager, serviceContext, method, args);
        Response response = requestHandler.submitRequest(request).get();
        return getOrThrowFromResponse(serializationManager, response);
    }

    public Object invokeServiceMethod(ServiceMethod method, Object handler, Object[] args)
            throws Exception {
        return method.invoke(handler, args);
    }
}
