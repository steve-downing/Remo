package org.stevedowning.remo.internal.common.responsehandlers;

@FunctionalInterface
public interface Callback<T> {
    public void handleResponse(Result<T> response);
}
