package org.stevedowning.remo.internal.common.invocation;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Method;

import org.stevedowning.remo.internal.common.request.InvocationRequest;
import org.stevedowning.remo.internal.common.request.Request;
import org.stevedowning.remo.internal.common.response.Response;
import org.stevedowning.remo.internal.common.serial.SerializationManager;
import org.stevedowning.remo.internal.common.service.ServiceContext;
import org.stevedowning.remo.internal.common.service.ServiceMethod;
import org.stevedowning.remo.internal.common.service.ServiceMethodId;
import org.suporma.idyll.id.Id;
import org.suporma.idyll.util.IdFactory;

public interface MethodInvocationStrategy {
    public boolean canHandle(Method method);
    // TODO: Provide a function that takes a method and tries to provide useful feedback about
    //       why this strategy can't handle that method.
    //       For example, suppose it's a function where one of the param types isn't Serializable
    //       or it returns a type of Guava Future that isn't supported.
    public Object handleClientInvocation(IdFactory idFactory, RequestHandler requestHandler,
            SerializationManager serializationManager, ServiceContext serviceContext,
            Method method, Object[] args) throws Throwable;
    public Object invokeServiceMethod(ServiceMethod method, Object handler, Object[] args)
            throws Exception;
    
    default InvocationRequest createRequest(IdFactory idFactory,
            SerializationManager serializationManager, ServiceContext serviceContext,
            Method method, Object[] args) throws IOException {
        Id<Request> requestId = idFactory.generateId();
        String[] serializedArgs = new String[args.length];
        for (int i = 0; i < args.length; ++i) {
            serializedArgs[i] = serializationManager.serialize(args[i]);
        }
        return new InvocationRequest(requestId, new ServiceMethodId(method), serializedArgs);
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
        if (klass.isPrimitive()) return true;
        return Serializable.class.isAssignableFrom(klass);
    }
    
    default Object getOrThrowFromResponse(SerializationManager serializationManager,
            Response response) throws Throwable {
        Object val = serializationManager.deserialize(response.getSerializedResult());
        if (!response.isSuccess() && val instanceof Throwable) {
            throw (Throwable)val;
        } else {
            return val;
        }
    }
    
    default boolean isBetweenInClassHierarchy(
            Class<?> superclass, Class<?> subclass, Class<?> klass) {
        return superclass.isAssignableFrom(klass) && klass.isAssignableFrom(subclass);
    }
}
