package org.stevedowning.remo.server.runner;

import java.io.IOException;

public interface ServiceRunner {
    public ServiceHandle runService(Object service, Class<?> serviceInterface, int port)
            throws IOException;
}
