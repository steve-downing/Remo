package org.stevedowning.remo.common.responsehandlers;

@FunctionalInterface
public interface Callback<T> {
    public void handleResponse(Result<T> response);
}
