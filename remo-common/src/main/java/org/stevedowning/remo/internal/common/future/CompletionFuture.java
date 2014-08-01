package org.stevedowning.remo.internal.common.future;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import org.stevedowning.remo.internal.common.future.observable.ObservableValue;
import org.stevedowning.remo.internal.common.future.observable.Observer;

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
    
    private static class CompletionObserver<T> implements Observer<T> {
        private final ObservableValue<T> observable;
        private final Predicate<T> completionCondition;
        private final CompletionFuture future;
        
        public CompletionObserver(ObservableValue<T> observable, Predicate<T> completionCondition,
                CompletionFuture future) {
            this.observable = observable;
            this.completionCondition = completionCondition;
            this.future = future;
        }
        
        public void handleChange(T val) {
            if (completionCondition.test(val)) {
                observable.detach(this);
                future.setCompleted();
            }
        }
    }
    
    public static <T> CompletionFuture getCompletionFuture(ObservableValue<T> observable,
            Predicate<T> completionCondition) {
        CompletionFuture future = new CompletionFuture();
        observable.attach(new CompletionObserver<T>(observable, completionCondition, future));
        return future;
    }
}
