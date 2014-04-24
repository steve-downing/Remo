package org.stevedowning.remo.common.response;

import org.stevedowning.commons.idyll.Id;
import org.stevedowning.remo.common.request.Request;

public class Response {
    private final Id<Request> requestId;
    private final String serializedResult;
    private final boolean isError;
    
    public Response(Id<Request> requestId, String serializedResult, boolean isError) {
        this.requestId = requestId;
        this.serializedResult = serializedResult;
        this.isError = isError;
    }
    
    public Id<Request> getRequestId() { return requestId; }
    public String getSerializedResult() { return serializedResult; }
    public boolean isError() { return isError; }
}
