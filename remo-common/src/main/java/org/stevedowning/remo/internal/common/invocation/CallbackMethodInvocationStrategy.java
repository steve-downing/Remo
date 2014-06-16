package org.stevedowning.remo.internal.common.invocation;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutionException;

import org.stevedowning.commons.idyll.Id;
import org.stevedowning.commons.idyll.idfactory.IdFactory;
import org.stevedowning.remo.Future;
import org.stevedowning.remo.internal.common.request.Request;
import org.stevedowning.remo.internal.common.serial.SerializationManager;
import org.stevedowning.remo.internal.common.service.ServiceContext;
import org.stevedowning.remo.internal.common.service.ServiceMethodId;

public class CallbackMethodInvocationStrategy implements MethodInvocationStrategy {
    public boolean canHandle(Method method) {
        return method.getReturnType().equals(Future.class);
    }

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
        return requestHandler.submitRequest(request);
    }

    @Override
    public Object invokeServiceMethod(Method method, Object handler,
            Object[] args) throws Exception {
        return ((Future<?>)method.invoke(handler, args)).get();
    }
}
