package org.stevedowning.remo.server.service;

import java.lang.reflect.Method;

import org.stevedowning.commons.idyll.datastructures.HashIdMap;
import org.stevedowning.commons.idyll.datastructures.IdMap;
import org.stevedowning.remo.common.service.ServiceMethod;
import org.stevedowning.remo.common.service.ServiceMethodId;

public class ServiceInterface {
    private final Object handler;
    private final IdMap<ServiceMethod> methodMap;
    
    public ServiceInterface(Class<?> serviceContract, Object handler) {
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
