package org.stevedowning.remo.client.internal;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import org.stevedowning.remo.client.internal.service.ServiceContext;
import org.stevedowning.remo.client.internal.service.ServiceHook;
import org.stevedowning.remo.client.internal.service.ServiceProxy;

public class DefaultRemoClientFactory implements RemoClientFactory {
    @SuppressWarnings("unchecked") // The cast should work just fine.
    public <T> T getRemoteService(Class<T> serviceType, String hostname, int port)
            throws IOException {
        Class<?>[] serviceTypes = new Class<?>[] { serviceType };
        InvocationHandler serviceProxy =
                new ServiceProxy(conn, serializationManager, ServiceContext.getBaseContext());
        return (T)Proxy.newProxyInstance(
                Thread.currentThread().getContextClassLoader(), serviceTypes, serviceProxy);
    }

    public <T> ServiceHook<T> createRemoteServiceHook(Class<T> serviceType, String hostname,
            int port) {
        // TODO Auto-generated method stub
        return null;
    }

}
