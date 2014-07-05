package org.stevedowning.remo.internal.common.invocation;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutionException;

import org.stevedowning.commons.idyll.idfactory.IdFactory;
import org.stevedowning.remo.internal.common.serial.SerializationManager;
import org.stevedowning.remo.internal.common.service.ServiceContext;
import org.stevedowning.remo.internal.common.service.ServiceMethod;

public class NoMethodInvocationStrategy implements MethodInvocationStrategy {
    public boolean canHandle(Method method) { return true; }

    public Object handleClientInvocation(IdFactory idFactory, RequestHandler requestHandler,
            SerializationManager serializationManager,
            ServiceContext serviceContext, Method method, Object[] args)
            throws IOException, InterruptedException, ExecutionException {
        throw new IOException("No strategy was found to handle this type of method call.");
    }

    public Object invokeServiceMethod(ServiceMethod method, Object handler, Object[] args)
            throws UnsupportedOperationException {
        throw new UnsupportedOperationException(
                "This strategy should never be invoked on the server");
    }
}
