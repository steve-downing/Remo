package org.stevedowning.remo.internal.client.invocation;

import org.stevedowning.remo.internal.common.future.Future;
import org.stevedowning.remo.internal.common.request.Request;

public interface RequestHandler {
    public Future<?> submitRequest(Request request);
}
