package org.stevedowning.remo;

import java.io.IOException;

public interface ServiceRunner {
    // TODO: Don't require the user to explicitly pass in a serviceContract.
    //       If they don't, just assume that all of handler's interfaces should be used.
    public <T> ServiceHandle runService(T handler, Class<T> serviceContract, int port)
            throws IOException;
}
