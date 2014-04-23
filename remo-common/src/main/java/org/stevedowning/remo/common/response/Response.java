package org.stevedowning.remo.common.response;

import org.stevedowning.commons.idyll.Id;
import org.stevedowning.remo.common.request.Request;

public class Response {
    private final Id<Request> requestId;
    private final String serializedResult;
    
    public Response(Id<Request> requestId, String serializedResult) {
        this.requestId = requestId;
        this.serializedResult = serializedResult;
    }
    
    public Id<Request> getRequestId() { return requestId; }
    public String getSerializedResult() { return serializedResult; }
}
