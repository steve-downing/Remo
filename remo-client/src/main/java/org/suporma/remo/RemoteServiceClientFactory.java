package org.suporma.remo;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import org.suporma.remo.internal.client.conn.RemoteServerConnection;
import org.suporma.remo.internal.client.conn.ServerConnection;
import org.suporma.remo.internal.client.service.ServiceProxy;
import org.suporma.remo.internal.common.serial.DefaultSerializationManager;
import org.suporma.remo.internal.common.serial.SerializationManager;
import org.suporma.remo.internal.common.service.ServiceContext;

public class RemoteServiceClientFactory implements ClientFactory {
    private final String hostname;
    private final int port;
    
    public RemoteServiceClientFactory(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }
    
    @SuppressWarnings("unchecked") // The cast should work just fine.
    public <T> T getService(Class<T> serviceType)
            throws IOException {
        Class<?>[] serviceTypes = new Class<?>[] { serviceType };
        ServerConnection conn = new RemoteServerConnection(hostname, port);
        SerializationManager serializationManager = new DefaultSerializationManager();
        InvocationHandler serviceProxy =
                new ServiceProxy(conn, serializationManager, ServiceContext.getBaseContext());
        return (T)Proxy.newProxyInstance(
                Thread.currentThread().getContextClassLoader(), serviceTypes, serviceProxy);
    }

    public <T> ServiceHook<T> createServiceHook(Class<T> serviceType) {
        // TODO Auto-generated method stub
        return null;
    }
}
