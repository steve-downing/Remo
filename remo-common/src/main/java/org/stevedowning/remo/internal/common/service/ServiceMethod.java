package org.stevedowning.remo.internal.common.service;

import java.lang.reflect.Method;

import org.stevedowning.commons.idyll.Identifiable;
import org.stevedowning.remo.internal.common.invocation.MethodInvocationStrategy;

public class ServiceMethod implements Identifiable<ServiceMethod> {
    private final Method method;
    private final ServiceMethodId id;
    private final MethodInvocationStrategy invocationStrategy;
    
    public ServiceMethod(Method method, MethodInvocationStrategy invocationStrategy) {
        this.id = new ServiceMethodId(method);
        this.method = method;
        this.invocationStrategy = invocationStrategy;
    }

    public ServiceMethodId getId() { return id; }
    public Method getMethod() { return method; }
    public MethodInvocationStrategy getInvocationStrategy() { return invocationStrategy; }
}
