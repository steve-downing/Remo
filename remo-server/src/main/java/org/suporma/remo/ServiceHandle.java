package org.suporma.remo;

import org.suporma.remo.internal.common.future.CompletionFuture;

public interface ServiceHandle {
    public CompletionFuture safeShutdown();
    public boolean isRunning();
}
