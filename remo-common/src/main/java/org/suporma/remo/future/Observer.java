package org.suporma.remo.future;

public interface Observer<T> {
    public void handleChange(T val);
}
