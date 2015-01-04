package org.suporma.remo.internal.common.future;

import java.util.concurrent.ExecutionException;

public class ErrorContainer {
    private volatile InterruptedException interruptedException;
    private volatile ExecutionException executionException;
    
    public ErrorContainer() {
        interruptedException = null;
        executionException = null;
    }
    
    public void setError(Throwable error) {
        if (error instanceof InterruptedException) {
            interruptedException = (InterruptedException)error;
        } else if (error instanceof ExecutionException) {
            executionException = (ExecutionException)error;
        } else {
            executionException = new ExecutionException(error);
        }
    }
    
    public void possiblyThrow() throws InterruptedException, ExecutionException {
        if (interruptedException != null) throw interruptedException;
        if (executionException != null) throw executionException;
    }
    
    public boolean hasError() {
        return interruptedException != null || executionException != null;
    }
}
