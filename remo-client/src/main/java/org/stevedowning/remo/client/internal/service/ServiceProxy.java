package org.stevedowning.remo.client.internal.service;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.stevedowning.commons.idyll.idfactory.IdFactory;
import org.stevedowning.commons.idyll.idfactory.LongIdFactory;
import org.stevedowning.remo.client.internal.service.conn.ServerConnection;
import org.stevedowning.remo.client.internal.service.invocation.MethodInvocationStrategy;
import org.stevedowning.remo.client.internal.service.invocation.MethodInvocationStrategySelector;
import org.stevedowning.remo.common.request.Request;
import org.stevedowning.remo.common.serial.SerializationManager;

public class ServiceProxy implements InvocationHandler {
    private final ServerConnection conn;
    private final SerializationManager serializationManager;
    private final ServiceContext serviceContext;
    private final IdFactory idFactory;
    
    public ServiceProxy(ServerConnection conn, SerializationManager serializationManager,
            ServiceContext serviceContext) {
        this.conn = conn;
        this.serializationManager = serializationManager;
        this.serviceContext = serviceContext;
        this.idFactory = new LongIdFactory();
    }

    public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable {
        MethodInvocationStrategy strategy =
                new MethodInvocationStrategySelector().getStrategy(method);
        if (args == null) {
            args = new Object[] {};
        }
        return strategy.getVal(idFactory, requestHandler, serializationManager, serviceContext,
                method, args);
    }

}
