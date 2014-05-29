package org.stevedowning.remo.client.internal;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import org.stevedowning.remo.client.internal.service.ServiceContext;
import org.stevedowning.remo.client.internal.service.ServiceHook;
import org.stevedowning.remo.client.internal.service.ServiceProxy;
import org.stevedowning.remo.client.internal.service.conn.DefaultServerConnection;
import org.stevedowning.remo.client.internal.service.conn.ServerConnection;
import org.stevedowning.remo.common.serial.DefaultSerializationManager;
import org.stevedowning.remo.common.serial.SerializationManager;

public class DefaultRemoClientFactory implements RemoClientFactory {
    @SuppressWarnings("unchecked") // The cast should work just fine.
    public <T> T getRemoteService(Class<T> serviceType, String hostname, int port)
            throws IOException {
        Class<?>[] serviceTypes = new Class<?>[] { serviceType };
        ServerConnection conn = new DefaultServerConnection(hostname, port);
        SerializationManager serializationManager = new DefaultSerializationManager();
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
