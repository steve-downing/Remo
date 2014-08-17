package org.stevedowning.remo.internal.common.invocation;

import java.lang.reflect.Method;

import org.stevedowning.commons.idyll.idfactory.IdFactory;
import org.stevedowning.remo.internal.common.request.Request;
import org.stevedowning.remo.internal.common.response.Response;
import org.stevedowning.remo.internal.common.serial.SerializationManager;
import org.stevedowning.remo.internal.common.service.ServiceContext;
import org.stevedowning.remo.internal.common.service.ServiceMethod;

public class SimpleMethodInvocationStrategy implements MethodInvocationStrategy {
    public boolean canHandle(Method method) {
        return areArgsAndReturnTypeSerializable(method);
    }

    public Object handleClientInvocation(IdFactory idFactory, RequestHandler requestHandler,
            SerializationManager serializationManager,
            ServiceContext serviceContext, Method method, Object[] args) throws Throwable {
        Request request = createRequest(
                idFactory, serializationManager, serviceContext, method, args);
        Response response = requestHandler.submitRequest(request).get();
        Object val = serializationManager.deserialize(response.getSerializedResult());
        if (!response.isSuccess() && val instanceof Throwable) {
            throw (Throwable)val;
        } else {
            return val;
        }
    }

    public Object invokeServiceMethod(ServiceMethod method, Object handler, Object[] args)
            throws Exception {
        return method.invoke(handler, args);
    }
}
