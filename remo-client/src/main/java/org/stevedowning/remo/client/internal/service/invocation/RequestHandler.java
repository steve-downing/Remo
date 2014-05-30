package org.stevedowning.remo.client.internal.service.invocation;

import org.stevedowning.remo.common.future.Future;
import org.stevedowning.remo.common.request.Request;

public interface RequestHandler {
    public Future<?> submitRequest(Request request);
}
