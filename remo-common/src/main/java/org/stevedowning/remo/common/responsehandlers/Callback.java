package org.stevedowning.remo.common.responsehandlers;

public interface Callback<T> {
    public void handleResponse(T response);
    public void handleError(Exception e);
}
