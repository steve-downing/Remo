package org.stevedowning.remo.internal.common;

public interface CancellationAction {
    public default void run() { run(true); }
    public void run(boolean mayInterruptIfRunning);
}
