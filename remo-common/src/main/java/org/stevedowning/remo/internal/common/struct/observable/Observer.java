package org.stevedowning.remo.internal.common.struct.observable;

public interface Observer<T> {
    public void handleChange(T val);
}
