package org.stevedowning.remo.internal.common.service;

import java.lang.reflect.Method;

import org.stevedowning.commons.idyll.Identifiable;

public class ServiceMethod implements Identifiable<ServiceMethod> {
    private final Method method;
    private final ServiceMethodId id;
    
    public ServiceMethod(Method method) {
        this.id = new ServiceMethodId(method);
        this.method = method;
    }

    public ServiceMethodId getId() { return id; }
    public Method getMethod() { return method; }
}
