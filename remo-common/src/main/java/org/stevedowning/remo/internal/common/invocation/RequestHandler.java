package org.stevedowning.remo.internal.common.invocation;

import org.stevedowning.remo.Future;
import org.stevedowning.remo.internal.common.request.Request;
import org.stevedowning.remo.internal.common.response.Response;

public interface RequestHandler {
    public Future<Response> submitRequest(Request request);
}
