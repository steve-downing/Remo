package org.stevedowning.remo.internal.common.future.observable;

public interface Observer<T> {
    public void handleChange(T val);
}
