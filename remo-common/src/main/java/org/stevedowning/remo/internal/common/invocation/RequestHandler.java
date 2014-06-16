package org.stevedowning.remo.internal.common.invocation;

import org.stevedowning.remo.Future;
import org.stevedowning.remo.internal.common.request.Request;

public interface RequestHandler {
    // TODO: Maybe this would make more sense if it returned a Future<Response>.
    public Future<?> submitRequest(Request request);
}
