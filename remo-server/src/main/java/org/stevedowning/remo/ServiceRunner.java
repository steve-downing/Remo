package org.stevedowning.remo;

import java.io.IOException;
import java.util.concurrent.Executor;

public interface ServiceRunner {
    // TODO: Don't require the user to explicitly pass in a serviceContract.
    //       If they don't, just assume that all of handler's interfaces should be used.
    //       Make sure to verify that the handler implements at least one interface though.
    // TODO: Allow an optional "path". This would allow clients to, for example, access
    //       multiples of the same type of service.
    public <T> ServiceHandle runService(T handler, Class<T> serviceContract, int port)
            throws IOException;
    public ServiceRunner useExecutor(Executor executor);
}
