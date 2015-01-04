package org.suporma.remo;

@FunctionalInterface
public interface Callback<T> {
    public void handleResult(Result<T> result);
}
