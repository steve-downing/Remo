package org.stevedowning.remo.internal.client.service;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.stevedowning.commons.idyll.idfactory.IdFactory;
import org.stevedowning.commons.idyll.idfactory.LongIdFactory;
import org.stevedowning.remo.internal.client.conn.ServerConnection;
import org.stevedowning.remo.internal.common.ClientId;
import org.stevedowning.remo.internal.common.invocation.MethodInvocationStrategy;
import org.stevedowning.remo.internal.common.invocation.MethodInvocationStrategySelector;
import org.stevedowning.remo.internal.common.invocation.RequestHandler;
import org.stevedowning.remo.internal.common.serial.SerializationManager;
import org.stevedowning.remo.internal.common.service.ServiceContext;

public class ServiceProxy implements InvocationHandler {
    private final ClientId clientId;
    private final ServerConnection conn;
    private final SerializationManager serializationManager;
    private final ServiceContext serviceContext;
    private final IdFactory idFactory;
    
    public ServiceProxy(ServerConnection conn, SerializationManager serializationManager,
            ServiceContext serviceContext) {
        this.clientId = ClientId.generate();
        this.conn = conn;
        this.serializationManager = serializationManager;
        this.serviceContext = serviceContext;
        this.idFactory = new LongIdFactory();
    }

    public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable {
        MethodInvocationStrategy strategy =
                new MethodInvocationStrategySelector().getStrategy(method);
        if (args == null) args = new Object[] {};
        // TODO: Be smarter about batching instead of sending one batch per request.
        RequestHandler requestHandler = new SimpleRequestHandler(clientId, idFactory, conn);
        // TODO: This is an unwieldy number of args. Find a way to inject some of them instead.
        return strategy.handleClientInvocation(idFactory, requestHandler, serializationManager,
                serviceContext, method, args);
    }
}
