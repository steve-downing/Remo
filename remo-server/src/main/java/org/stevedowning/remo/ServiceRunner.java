package org.stevedowning.remo;

import java.io.IOException;

public interface ServiceRunner {
    public <T> ServiceHandle runService(T handler, Class<T> serviceContract, int port)
            throws IOException;
}
