package org.suporma.remo.internal.common.struct.observable;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ObservableValue<T> {
    private T val;
    private final Queue<Observer<T>> observers;
    
    public ObservableValue(T val) {
        this.val = val;
        this.observers = new ConcurrentLinkedQueue<>();
    }
    
    public T get() { return val; }
    
    public synchronized void set(T val) {
        this.val = val;
        for (Observer<T> observer; (observer = observers.poll()) != null;) {
            observer.handleChange(val);
        }
    }
    
    public ObservableValue<T> attach(Observer<T> observer) {
        if (observer != null) observers.add(observer);
        return this;
    }
    
    public ObservableValue<T> detach(Observer<T> observer) {
        observers.remove(observer);
        return this;
    }
}
