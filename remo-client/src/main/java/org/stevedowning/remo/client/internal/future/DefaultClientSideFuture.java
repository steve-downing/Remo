package org.stevedowning.remo.client.internal.future;

import org.stevedowning.remo.common.future.BasicFuture;
import org.stevedowning.remo.common.responsehandlers.CancelOptions;
import org.stevedowning.remo.common.responsehandlers.CancelResult;
import org.stevedowning.remo.common.responsehandlers.Future;

public class DefaultClientSideFuture<T> extends BasicFuture<T> {
    public Future<CancelResult> cancel(CancelOptions options) {
        // TODO: Fill this in. This should cancel the request on the client, and optionally on the
        //       service.
        // TODO: Should this return and object that keeps track of the various stages of
        //       cancellation success and failure?
        cancel();
        DefaultClientSideFuture<CancelResult> future = new DefaultClientSideFuture<CancelResult>();
        future.setVal(CancelResult.CANCEL_ON_CLIENT_ONLY);
        return future;
    }
}
