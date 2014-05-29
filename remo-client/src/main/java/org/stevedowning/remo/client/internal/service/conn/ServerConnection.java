package org.stevedowning.remo.client.internal.service.conn;

import org.stevedowning.remo.common.request.RequestBatch;
import org.stevedowning.remo.common.response.ResponseBatch;
import org.stevedowning.remo.common.responsehandlers.Future;

public interface ServerConnection {
    public Future<ResponseBatch> send(final RequestBatch requestBatch);
}
