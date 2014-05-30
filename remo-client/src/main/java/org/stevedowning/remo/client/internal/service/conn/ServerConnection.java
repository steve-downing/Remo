package org.stevedowning.remo.client.internal.service.conn;

import org.stevedowning.remo.common.future.Future;
import org.stevedowning.remo.common.request.ConnectionRequestBatch;
import org.stevedowning.remo.common.response.ConnectionResponseBatch;

public interface ServerConnection {
    public Future<ConnectionResponseBatch> send(final ConnectionRequestBatch requestBatch);
}
