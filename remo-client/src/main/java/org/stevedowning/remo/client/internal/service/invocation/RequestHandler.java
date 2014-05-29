package org.stevedowning.remo.client.internal.service.invocation;

import org.stevedowning.remo.common.request.Request;
import org.stevedowning.remo.common.responsehandlers.Future;

public interface RequestHandler {
    public Future<?> submitRequest(Request request);
}
