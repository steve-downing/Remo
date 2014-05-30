package org.stevedowning.remo.client.internal.service.invocation;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutionException;

import org.stevedowning.commons.idyll.Id;
import org.stevedowning.commons.idyll.idfactory.IdFactory;
import org.stevedowning.remo.client.internal.service.ServiceContext;
import org.stevedowning.remo.common.request.ConnectionRequest;
import org.stevedowning.remo.common.serial.SerializationManager;
import org.stevedowning.remo.common.service.ServiceMethodId;

public class SimpleMethodInvocationStrategy implements MethodInvocationStrategy {
    public boolean canHandle(Method method) { return true; }

    @Override
    public Object getVal(IdFactory idFactory, RequestHandler requestHandler,
            SerializationManager serializationManager,
            ServiceContext serviceContext, Method method, Object[] args)
                    throws IOException, InterruptedException, ExecutionException {
        Id<ConnectionRequest> requestId = idFactory.generateId();
        String[] serializedArgs = new String[args.length];
        for (int i = 0; i < args.length; ++i) {
            serializedArgs[i] = serializationManager.serialize(args[i]);
        }
        ConnectionRequest request = new ConnectionRequest(requestId, new ServiceMethodId(method), serializedArgs);
        return requestHandler.submitRequest(request).get();
    }
}
