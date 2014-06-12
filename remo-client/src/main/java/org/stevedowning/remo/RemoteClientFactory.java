package org.stevedowning.remo;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import org.stevedowning.remo.internal.client.conn.DefaultServerConnection;
import org.stevedowning.remo.internal.client.conn.ServerConnection;
import org.stevedowning.remo.internal.client.service.ServiceProxy;
import org.stevedowning.remo.internal.common.serial.DefaultSerializationManager;
import org.stevedowning.remo.internal.common.serial.SerializationManager;
import org.stevedowning.remo.internal.common.service.ServiceContext;

public class RemoteClientFactory implements ClientFactory {
    private final String hostname;
    private final int port;
    
    public RemoteClientFactory(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }
    
    @SuppressWarnings("unchecked") // The cast should work just fine.
    public <T> T getRemoteService(Class<T> serviceType)
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
