package org.suporma.remo.internal.common.invocation;

import org.suporma.remo.Future;
import org.suporma.remo.internal.common.request.Request;
import org.suporma.remo.internal.common.response.Response;

public interface RequestHandler {
    public Future<Response> submitRequest(Request request);
}
