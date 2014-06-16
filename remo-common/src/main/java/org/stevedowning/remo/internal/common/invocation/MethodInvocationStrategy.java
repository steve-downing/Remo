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
    // Indicates whether getVal() can block on remote execution. This is important for batching
    // correctly. When the client creates an explicit batch block, we need to be smart and fire off
    // the batch if one of the calls blocks on a response from the service.
    // TODO: public boolean isBlockingCall();
}
