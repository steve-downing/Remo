package org.stevedowning.remo.internal.server.service;

import java.lang.reflect.Method;

import org.stevedowning.commons.idyll.datastructures.HashIdMap;
import org.stevedowning.commons.idyll.datastructures.IdMap;
import org.stevedowning.remo.internal.common.service.ServiceMethod;
import org.stevedowning.remo.internal.common.service.ServiceMethodId;

public class ServiceInterface {
    private final Object handler;
    private final IdMap<ServiceMethod> methodMap;
    
    public <T> ServiceInterface(Class<T> serviceContract, T handler) {
        this.handler = handler;
        this.methodMap = new HashIdMap<ServiceMethod>();
        populateMethodMap(serviceContract);
    }
    
    public void populateMethodMap(Class<?> interfaceType) {
        for (Method method : interfaceType.getMethods()) {
            methodMap.add(new ServiceMethod(method));
        }
    }
    
    public Object getResult(ServiceMethodId methodId, Object[] args) throws Exception {
        ServiceMethod serviceMethod = methodMap.get(methodId);
        if (serviceMethod == null) {
            throw new NoSuchMethodException();
        }
        return serviceMethod.getMethod().invoke(handler, args);
    }
}
