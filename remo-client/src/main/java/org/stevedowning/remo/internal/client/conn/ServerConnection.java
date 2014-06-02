package org.stevedowning.remo.internal.client.conn;

import org.stevedowning.remo.internal.common.future.Future;
import org.stevedowning.remo.internal.common.request.RequestBatch;
import org.stevedowning.remo.internal.common.response.ResponseBatch;

public interface ServerConnection {
    public Future<ResponseBatch> send(final RequestBatch requestBatch);
}
