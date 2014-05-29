package org.stevedowning.remo.client.internal.service.invocation;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.stevedowning.commons.idyll.Id;
import org.stevedowning.commons.idyll.idfactory.IdFactory;
import org.stevedowning.remo.client.internal.future.DefaultClientSideFuture;
import org.stevedowning.remo.client.internal.service.ServiceContext;
import org.stevedowning.remo.client.internal.service.conn.ServerConnection;
import org.stevedowning.remo.common.request.Request;
import org.stevedowning.remo.common.serial.SerializationManager;
import org.stevedowning.remo.common.service.ServiceMethodId;

public class SimpleMethodInvocationStrategy implements MethodInvocationStrategy {
    public boolean canHandle(Method method) { return true; }

    @Override
    public Object getVal(IdFactory idFactory, RequestHandler requestHandler,
            SerializationManager serializationManager,
            ServiceContext serviceContext, Method method, Object[] args)
                    throws IOException, InterruptedException, ExecutionException {
        Id<Request> requestId = idFactory.generateId();
        String[] serializedArgs = new String[args.length];
        for (int i = 0; i < args.length; ++i) {
            serializedArgs[i] = serializationManager.serialize(args[i]);
        }
        Request request = new Request(requestId, new ServiceMethodId(method), serializedArgs);
        return requestHandler.submitRequest(request).get();
    }

    private <T> Object handleMethodInvocationImpl(ServerConnection conn,
            final SerializationManager serializationManager,
            ServiceContext serviceContext, Method method,
            Object[] argArr) throws Exception {
        List<Object> args = new LinkedList<Object>();
        for (int i = 0; i < argArr.length; ++i) {
            args.add(argArr[i]);
        }
        final DefaultClientSideFuture<T> future = new DefaultClientSideFuture<T>();
        String requestStr = invocationSerializer.serializeClientInvocationArgs(
                method, args, superserviceContext);
        conn.send(requestStr).addCallback(new BasicCallback<String>() {
            public void handleException(Exception ex) {
                future.setException(ex);
            }
            @SuppressWarnings("unchecked")
            public void handleResponse(String responseStr) {
                try {
                    T responseObj =
                            (T)(invocationSerializer.deserializeServiceResponse(responseStr));
                    future.setVal(responseObj);
                } catch (Exception ex) {
                    future.setException(ex);
                }
            }
        });
        return future.blockAndGet();
    }

    @Override
    public Object handleMethodInvocation(ServerConnection conn,
            MethodInvocationSerializer invocationSerializer,
            SuperserviceContext superserviceContext, Method method, Object[] argArr)
                    throws Exception {
        return handleMethodInvocationImpl(
                conn, invocationSerializer, superserviceContext, method, argArr);
    }
}
