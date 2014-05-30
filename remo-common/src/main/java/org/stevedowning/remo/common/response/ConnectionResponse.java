package org.stevedowning.remo.common.response;

import org.stevedowning.commons.idyll.Id;
import org.stevedowning.remo.common.request.ConnectionRequest;

public class ConnectionResponse {
    private final Id<ConnectionRequest> requestId;
    private final String serializedResult;
    private final boolean isSuccess;
    
    public ConnectionResponse(Id<ConnectionRequest> requestId, String serializedResult,
            boolean isSuccess) {
        this.requestId = requestId;
        this.serializedResult = serializedResult;
        this.isSuccess = isSuccess;
    }
    
    public Id<ConnectionRequest> getRequestId() { return requestId; }
    public String getSerializedResult() { return serializedResult; }
    public boolean isSuccess() { return isSuccess; }
}
