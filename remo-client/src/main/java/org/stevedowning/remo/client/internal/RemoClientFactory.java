package org.stevedowning.remo.client.internal;

import java.io.IOException;

import org.stevedowning.remo.client.internal.service.ServiceHook;

public interface RemoClientFactory {
    public <T> T getRemoteService(Class<T> serviceType, String hostname, int port)
            throws IOException;
    public <T> ServiceHook<T> createRemoteServiceHook(Class<T> serviceType, String hostname,
            int port) throws IOException;
}
