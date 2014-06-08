package org.stevedowning.remo.internal.client.service;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.stevedowning.commons.idyll.idfactory.IdFactory;
import org.stevedowning.commons.idyll.idfactory.LongIdFactory;
import org.stevedowning.remo.internal.client.conn.ServerConnection;
import org.stevedowning.remo.internal.client.invocation.MethodInvocationStrategy;
import org.stevedowning.remo.internal.client.invocation.MethodInvocationStrategySelector;
import org.stevedowning.remo.internal.client.invocation.RequestHandler;
import org.stevedowning.remo.internal.common.request.Request;
import org.stevedowning.remo.internal.common.request.RequestBatch;
import org.stevedowning.remo.internal.common.response.ResponseBatch;
import org.stevedowning.remo.internal.common.serial.SerializationManager;

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
        RequestHandler requestHandler = (Request request) -> {
            RequestBatch requestBatch =
                    new RequestBatch(idFactory.generateId());
            requestBatch.add(request);
            return conn.send(requestBatch).transform((ResponseBatch responseBatch) -> {
                String resultStr = responseBatch.get(request.getId()).getSerializedResult();
                return serializationManager.deserialize(resultStr);
            });
        };
        return strategy.getVal(idFactory, requestHandler, serializationManager, serviceContext,
                method, args);
    }
}
