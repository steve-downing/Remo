package org.stevedowning.remo.internal.client.service;

import org.stevedowning.commons.idyll.idfactory.IdFactory;
import org.stevedowning.remo.Future;
import org.stevedowning.remo.internal.client.conn.ServerConnection;
import org.stevedowning.remo.internal.common.invocation.RequestHandler;
import org.stevedowning.remo.internal.common.request.Request;
import org.stevedowning.remo.internal.common.request.RequestBatch;
import org.stevedowning.remo.internal.common.response.Response;
import org.stevedowning.remo.internal.common.response.ResponseBatch;

public class SimpleRequestHandler implements RequestHandler {
    private final IdFactory idFactory;
    private final ServerConnection conn;
    
    public SimpleRequestHandler(IdFactory idFactory, ServerConnection conn) {
        this.idFactory = idFactory;
        this.conn = conn;
    }
    
    public Future<Response> submitRequest(Request request) {
        RequestBatch requestBatch = new RequestBatch(idFactory.generateId());
        requestBatch.add(request);
        return conn.send(requestBatch).transform((ResponseBatch responseBatch) -> {
            return responseBatch.get(request.getId());
        });
    }
}
