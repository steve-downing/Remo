package org.stevedowning.remo.common.request;

import org.stevedowning.commons.idyll.Id;
import org.stevedowning.commons.idyll.Identifiable;
import org.stevedowning.remo.common.service.ServiceMethodId;

public class ConnectionRequest implements Identifiable<ConnectionRequest> {
    private final Id<ConnectionRequest> id;
    private final ServiceMethodId methodId;
    private final String[] serializedParams;
    
    public ConnectionRequest(Id<ConnectionRequest> id, ServiceMethodId methodId, String[] serializedParams) {
        this.id = id;
        this.methodId = methodId;
        this.serializedParams = serializedParams;
    }
    
    public Id<ConnectionRequest> getId() { return id; }
    public ServiceMethodId getMethodId() { return methodId; }
    public String[] getSerializedParams() { return serializedParams; }
}
