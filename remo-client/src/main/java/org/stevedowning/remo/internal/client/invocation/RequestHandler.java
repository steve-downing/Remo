package org.stevedowning.remo.internal.client.invocation;

import org.stevedowning.remo.common.future.Future;
import org.stevedowning.remo.common.request.Request;

public interface RequestHandler {
    public Future<?> submitRequest(Request request);
}
