package org.suporma.remo;

import java.io.IOException;

public interface ClientFactory {
    public <T> T getService(Class<T> serviceType) throws IOException;
    public <T> ServiceHook<T> createServiceHook(Class<T> serviceType) throws IOException;
}
