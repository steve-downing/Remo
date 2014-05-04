package org.stevedowning.remo.client.internal.service.conn;

import org.stevedowning.remo.common.request.RequestBatch;
import org.stevedowning.remo.common.response.ResponseBatch;

public interface ServerConnection {
    public CancellableFuture<ResponseBatch> send(final RequestBatch requestBatch);
}
