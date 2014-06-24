package org.stevedowning.remo.internal.common.invocation;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutionException;

import org.stevedowning.commons.idyll.Id;
import org.stevedowning.commons.idyll.idfactory.IdFactory;
import org.stevedowning.remo.internal.common.request.Request;
import org.stevedowning.remo.internal.common.serial.SerializationManager;
import org.stevedowning.remo.internal.common.service.ServiceContext;
import org.stevedowning.remo.internal.common.service.ServiceMethodId;

public interface MethodInvocationStrategy {
    public boolean canHandle(Method method);
    public Object getVal(IdFactory idFactory, RequestHandler requestHandler,
            SerializationManager serializationManager, ServiceContext serviceContext,
            Method method, Object[] args)
                    throws IOException, InterruptedException, ExecutionException;
    public Object invokeServiceMethod(Method method, Object handler, Object[] args)
            throws Exception;
    
    default Request createRequest(IdFactory idFactory, SerializationManager serializationManager,
            ServiceContext serviceContext, Method method, Object[] args) throws IOException {
        Id<Request> requestId = idFactory.generateId();
        String[] serializedArgs = new String[args.length];
        for (int i = 0; i < args.length; ++i) {
            serializedArgs[i] = serializationManager.serialize(args[i]);
        }
        return new Request(requestId, new ServiceMethodId(method), serializedArgs);
    }
    
    default boolean areArgsAndReturnTypeSerializable(Method method) {
        if (!isSerializable(method.getReturnType())) return false;
        return areArgsSerializable(method);
    }
    
    default boolean areArgsSerializable(Method method) {
        for (Class<?> klass : method.getParameterTypes()) {
            if (!isSerializable(klass)) return false;
        }
        return true;
    }
    
    default boolean isSerializable(Class<?> klass) {
        if (klass.equals(Void.class)) return true;
        return Serializable.class.isAssignableFrom(klass);
    }
}
