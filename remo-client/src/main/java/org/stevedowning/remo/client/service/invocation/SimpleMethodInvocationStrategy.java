package org.stevedowning.remo.client.service.invocation;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import org.stevedowning.remo.client.service.conn.ServerConnection;

public class SimpleMethodInvocationStrategy implements MethodInvocationStrategy {
    public boolean canHandle(Method method) { return true; }

    private <T> Object handleMethodInvocationImpl(ServerConnection conn,
            final MethodInvocationSerializer invocationSerializer,
            SuperserviceContext superserviceContext, Method method,
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
