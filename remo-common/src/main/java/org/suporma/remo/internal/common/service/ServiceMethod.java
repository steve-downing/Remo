package org.suporma.remo.internal.common.service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.suporma.idyll.id.Identifiable;
import org.suporma.remo.internal.common.invocation.MethodInvocationStrategy;

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
    public MethodInvocationStrategy getInvocationStrategy() { return invocationStrategy; }
    public Object invoke(Object handler, Object[] args) throws Exception {
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
