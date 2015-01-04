package org.suporma.remo.internal.common.request;

import org.suporma.idyll.id.Id;
import org.suporma.remo.internal.common.service.ServiceMethodId;

public class InvocationRequest implements Request {
    private static final long serialVersionUID = -6513834117816347278L;

    private final Id<Request> id;
    private final ServiceMethodId methodId;
    private final String[] serializedParams;
    
    public InvocationRequest(Id<Request> id, ServiceMethodId methodId,
            String[] serializedParams) {
        this.id = id;
        this.methodId = methodId;
        this.serializedParams = serializedParams;
    }
    
    public Id<Request> getId() { return id; }
    public ServiceMethodId getMethodId() { return methodId; }
    public String[] getSerializedParams() { return serializedParams; }

    public void accept(RequestVisitor visitor) { visitor.visit(this); }
}
