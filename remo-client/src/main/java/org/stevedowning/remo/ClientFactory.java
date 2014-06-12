package org.stevedowning.remo;

import java.io.IOException;

public interface ClientFactory {
    public <T> T getRemoteService(Class<T> serviceType) throws IOException;
    public <T> ServiceHook<T> createRemoteServiceHook(Class<T> serviceType, String hostname,
            int port) throws IOException;
}
