package org.stevedowning.remo;

import java.io.IOException;

public interface ClientFactory {
    // TODO: Change these names to not imply remoteness.
    public <T> T getRemoteService(Class<T> serviceType) throws IOException;
    public <T> ServiceHook<T> createRemoteServiceHook(Class<T> serviceType) throws IOException;
}
