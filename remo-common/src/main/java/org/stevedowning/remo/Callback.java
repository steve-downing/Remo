package org.stevedowning.remo;

@FunctionalInterface
public interface Callback<T> {
    public void handleResult(Result<T> result);
}
