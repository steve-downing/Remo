package org.suporma.remo.internal.common.request;

import org.suporma.idyll.id.Id;

public class CancellationRequest implements Request {
    private static final long serialVersionUID = -8391600089252538774L;
    
    private final Id<Request> id, cancellationTargetId;
    
    public CancellationRequest(Id<Request> id, Id<Request> cancellationTargetId) {
        this.id = id;
        this.cancellationTargetId = cancellationTargetId;
    }
    
    public Id<Request> getCancellationTargetId() { return cancellationTargetId; }
    public Id<Request> getId() { return id; }

    public void accept(RequestVisitor visitor) { visitor.visit(this); }
}
