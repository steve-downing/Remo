package org.stevedowning.remo;

import org.stevedowning.remo.internal.common.future.CompletionFuture;

public interface ServiceHandle {
    public CompletionFuture safeShutdown();
    public boolean isRunning();
    // TODO: public ServiceHandle onReady(Runnable runnable);
}
