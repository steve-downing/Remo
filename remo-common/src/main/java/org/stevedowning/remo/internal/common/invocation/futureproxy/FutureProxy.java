package org.stevedowning.remo.internal.common.invocation.futureproxy;

import org.stevedowning.remo.internal.common.CancellationAction;

public interface FutureProxy {
    public void set(Object val) throws Exception;
    public void setException(Object ex) throws Exception;
    public void addCancellationAction(CancellationAction cancellationAction) throws Exception;
    public Object get() throws Exception;
    public Object getBackingFuture();
}
