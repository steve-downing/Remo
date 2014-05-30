package org.stevedowning.remo.client;

import java.io.IOException;

public interface RemoClientFactory {
    public <T> T getRemoteService(Class<T> serviceType, String hostname, int port)
            throws IOException;
    public <T> ServiceHook<T> createRemoteServiceHook(Class<T> serviceType, String hostname,
            int port) throws IOException;
}
