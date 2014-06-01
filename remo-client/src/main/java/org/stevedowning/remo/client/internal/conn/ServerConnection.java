package org.stevedowning.remo.client.internal.conn;

import org.stevedowning.remo.common.future.Future;
import org.stevedowning.remo.common.request.RequestBatch;
import org.stevedowning.remo.common.response.ResponseBatch;

public interface ServerConnection {
    public Future<ResponseBatch> send(final RequestBatch requestBatch);
}
