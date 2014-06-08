package org.stevedowning.remo.internal.common.request;

import java.io.Serializable;

import org.stevedowning.commons.idyll.Id;
import org.stevedowning.commons.idyll.Identifiable;
import org.stevedowning.remo.internal.common.service.ServiceMethodId;

public class Request implements Identifiable<Request>, Serializable {
    private static final long serialVersionUID = -6513834117816347278L;

    private final Id<Request> id;
    private final ServiceMethodId methodId;
    private final String[] serializedParams;
    
    public Request(Id<Request> id, ServiceMethodId methodId, String[] serializedParams) {
        this.id = id;
        this.methodId = methodId;
        this.serializedParams = serializedParams;
    }
    
    public Id<Request> getId() { return id; }
    public ServiceMethodId getMethodId() { return methodId; }
    public String[] getSerializedParams() { return serializedParams; }
}
