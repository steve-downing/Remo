package org.stevedowning.remo.internal.common.invocation;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutionException;

import org.stevedowning.commons.idyll.idfactory.IdFactory;
import org.stevedowning.remo.internal.common.serial.SerializationManager;
import org.stevedowning.remo.internal.common.service.ServiceContext;

public interface MethodInvocationStrategy {
    public boolean canHandle(Method method);
    public Object getVal(IdFactory idFactory, RequestHandler requestHandler,
            SerializationManager serializationManager, ServiceContext serviceContext,
            Method method, Object[] args)
                    throws IOException, InterruptedException, ExecutionException;
    public Object invokeServiceMethod(Method method, Object handler, Object[] args)
            throws Exception;
}
