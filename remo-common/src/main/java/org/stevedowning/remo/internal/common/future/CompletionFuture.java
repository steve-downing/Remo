package org.stevedowning.remo.internal.common.future;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class CompletionFuture extends BasicFuture<Void> {
    public void await() throws InterruptedException, ExecutionException {
        get();
    }
    
    public void await(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException {
        get(timeout, unit);
    }
    
    public void setCompleted() {
        setVal(null);
    }
}
