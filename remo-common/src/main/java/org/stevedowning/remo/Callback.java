package org.stevedowning.remo;

@FunctionalInterface
public interface Callback<T> {
    public void handleResponse(Result<T> response);
}
