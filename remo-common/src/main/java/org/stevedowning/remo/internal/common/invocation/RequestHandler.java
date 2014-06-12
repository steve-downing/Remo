package org.stevedowning.remo.internal.common.invocation;

import org.stevedowning.remo.Future;
import org.stevedowning.remo.internal.common.request.Request;

public interface RequestHandler {
    public Future<?> submitRequest(Request request);
}