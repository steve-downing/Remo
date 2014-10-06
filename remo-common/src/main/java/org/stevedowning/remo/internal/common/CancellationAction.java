package org.stevedowning.remo.internal.common;

public interface CancellationAction {
    public void run(boolean mayInterruptIfRunning);
}
