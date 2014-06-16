package org.stevedowning.remo.internal.client.service;

import org.stevedowning.commons.idyll.idfactory.IdFactory;
import org.stevedowning.remo.Future;
import org.stevedowning.remo.internal.client.conn.ServerConnection;
import org.stevedowning.remo.internal.common.invocation.RequestHandler;
import org.stevedowning.remo.internal.common.request.Request;
import org.stevedowning.remo.internal.common.request.RequestBatch;
import org.stevedowning.remo.internal.common.response.ResponseBatch;
import org.stevedowning.remo.internal.common.serial.SerializationManager;

public class SimpleRequestHandler implements RequestHandler {
    private final IdFactory idFactory;
    private final SerializationManager serializationManager;
    private final ServerConnection conn;
    
    public SimpleRequestHandler(IdFactory idFactory, SerializationManager serializationManager,
            ServerConnection conn) {
        this.idFactory = idFactory;
        this.serializationManager = serializationManager;
        this.conn = conn;
    }
    
    public Future<?> submitRequest(Request request) {
        RequestBatch requestBatch = new RequestBatch(idFactory.generateId());
        requestBatch.add(request);
        // TODO: It's possible that in the future, a ResponseBatch won't have a Response for every
        //       Request in the RequestBatch.
        return conn.send(requestBatch).transform((ResponseBatch responseBatch) -> {
            String resultStr = responseBatch.get(request.getId()).getSerializedResult();
            return serializationManager.deserialize(resultStr);
        });
    }
}
