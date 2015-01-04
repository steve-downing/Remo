package org.suporma.remo.internal.client.conn;

import org.suporma.remo.Future;
import org.suporma.remo.internal.common.request.RequestBatch;
import org.suporma.remo.internal.common.response.ResponseBatch;

public interface ServerConnection {
    public Future<ResponseBatch> send(final RequestBatch requestBatch);
}
