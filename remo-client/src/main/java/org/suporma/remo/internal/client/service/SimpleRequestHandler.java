package org.suporma.remo.internal.client.service;

import org.suporma.idyll.util.IdFactory;
import org.suporma.remo.Future;
import org.suporma.remo.internal.client.conn.ServerConnection;
import org.suporma.remo.internal.common.ClientId;
import org.suporma.remo.internal.common.invocation.RequestHandler;
import org.suporma.remo.internal.common.request.Request;
import org.suporma.remo.internal.common.request.RequestBatch;
import org.suporma.remo.internal.common.response.Response;
import org.suporma.remo.internal.common.response.ResponseBatch;

public class SimpleRequestHandler implements RequestHandler {
    private final ClientId clientId;
    private final IdFactory idFactory;
    private final ServerConnection conn;
    
    public SimpleRequestHandler(ClientId clientId, IdFactory idFactory, ServerConnection conn) {
        this.clientId = clientId;
        this.idFactory = idFactory;
        this.conn = conn;
    }
    
    public Future<Response> submitRequest(Request request) {
        RequestBatch requestBatch = new RequestBatch(clientId, idFactory.generateId());
        requestBatch.add(request);
        return conn.send(requestBatch).transform((ResponseBatch responseBatch) -> {
            return responseBatch.get(request.getId());
        });
    }
}
