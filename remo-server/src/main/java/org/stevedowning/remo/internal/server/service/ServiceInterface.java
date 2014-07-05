package org.stevedowning.remo.internal.server.service;

import java.lang.reflect.Method;

import org.stevedowning.commons.idyll.datastructures.HashIdMap;
import org.stevedowning.commons.idyll.datastructures.IdMap;
import org.stevedowning.remo.internal.common.invocation.MethodInvocationStrategy;
import org.stevedowning.remo.internal.common.invocation.MethodInvocationStrategySelector;
import org.stevedowning.remo.internal.common.service.ServiceMethod;
import org.stevedowning.remo.internal.common.service.ServiceMethodId;

public class ServiceInterface {
    private final Object handler;
    private final IdMap<ServiceMethod> methodMap;
    
    public <T> ServiceInterface(Class<T> serviceContract, T handler) {
        this.handler = handler;
        this.methodMap = new HashIdMap<ServiceMethod>();
        populateMethodMap(serviceContract, new MethodInvocationStrategySelector());
    }
    
    public void populateMethodMap(Class<?> interfaceType,
            MethodInvocationStrategySelector invocationStrategySelector) {
        // This goes through all public methods on the interface and precomputes which strategy
        // to apply when the method is called.
        for (Method method : interfaceType.getMethods()) {
            MethodInvocationStrategy strategy = invocationStrategySelector.getStrategy(method);
            methodMap.add(new ServiceMethod(method, strategy));
        }
    }
    
    public Object getResult(ServiceMethodId methodId, Object[] args) throws Exception {
        ServiceMethod serviceMethod = methodMap.get(methodId);
        if (serviceMethod == null) {
            throw new NoSuchMethodException();
        }
        MethodInvocationStrategy strategy = serviceMethod.getInvocationStrategy();
        return strategy.invokeServiceMethod(serviceMethod, handler, args);
    }
}
