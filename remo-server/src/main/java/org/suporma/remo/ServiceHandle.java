package org.suporma.remo;

import org.suporma.remo.future.CompletionFuture;

public interface ServiceHandle {
    public CompletionFuture safeShutdown();
    public boolean isRunning();
}
