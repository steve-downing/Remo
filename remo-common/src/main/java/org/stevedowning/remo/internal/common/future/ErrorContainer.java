package org.stevedowning.remo.internal.common.future;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class ErrorContainer {
    private volatile IOException ioException;
    private volatile InterruptedException interruptedException;
    private volatile ExecutionException executionException;
    
    public ErrorContainer() {
        ioException = null;
        interruptedException = null;
        executionException = null;
    }
    
    public void setError(Throwable error) {
        if (error instanceof IOException) {
            ioException = (IOException)error;
        } else if (error instanceof InterruptedException) {
            interruptedException = (InterruptedException)error;
        } else if (error instanceof ExecutionException) {
            executionException = (ExecutionException)error;
        } else {
            executionException = new ExecutionException(error);
        }
    }
    
    public void possiblyThrow() throws IOException, InterruptedException, ExecutionException {
        if (ioException != null) throw ioException;
        if (interruptedException != null) throw interruptedException;
        if (executionException != null) throw executionException;
    }
    
    public boolean hasError() {
        return ioException != null || interruptedException != null || executionException != null;
    }
}
