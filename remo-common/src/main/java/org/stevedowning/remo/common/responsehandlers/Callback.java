package org.stevedowning.remo.common.responsehandlers;

public interface Callback<T> {
    public void handleResponse(Response<T> response);
}
