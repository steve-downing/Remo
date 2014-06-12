package org.stevedowning.remo.internal.common.invocation;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutionException;

import org.stevedowning.commons.idyll.Id;
import org.stevedowning.commons.idyll.idfactory.IdFactory;
import org.stevedowning.remo.internal.common.request.Request;
import org.stevedowning.remo.internal.common.serial.SerializationManager;
import org.stevedowning.remo.internal.common.service.ServiceContext;
import org.stevedowning.remo.internal.common.service.ServiceMethodId;

public class SimpleMethodInvocationStrategy implements MethodInvocationStrategy {
    public boolean canHandle(Method method) { return true; }

    public Object getVal(IdFactory idFactory, RequestHandler requestHandler,
            SerializationManager serializationManager,
            ServiceContext serviceContext, Method method, Object[] args)
                    throws IOException, InterruptedException, ExecutionException {
        Id<Request> requestId = idFactory.generateId();
        String[] serializedArgs = new String[args.length];
        for (int i = 0; i < args.length; ++i) {
            serializedArgs[i] = serializationManager.serialize(args[i]);
        }
        Request request =
                new Request(requestId, new ServiceMethodId(method), serializedArgs);
        return requestHandler.submitRequest(request).get();
    }

    public Object invokeServiceMethod(Method method, Object handler, Object[] args)
            throws Exception {
        try {
            return method.invoke(handler, args);
        } catch (InvocationTargetException ex) {
            Throwable cause = ex.getCause();
            if (cause != null && cause instanceof Exception) {
                throw (Exception)cause;
            } else {
                throw ex;
            }
        }
    }
}
