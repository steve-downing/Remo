package org.suporma.remo.future;

import java.util.concurrent.Executor;

public class SameThreadExecutor implements Executor {
    public void execute(Runnable command) {
        command.run();
    }
}
