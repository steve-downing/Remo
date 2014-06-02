package org.stevedowning.remo.internal.common.response;

import org.stevedowning.commons.idyll.Id;
import org.stevedowning.remo.internal.common.request.Request;

public class Response {
    private final Id<Request> requestId;
    private final String serializedResult;
    private final boolean isSuccess;
    
    public Response(Id<Request> requestId, String serializedResult,
            boolean isSuccess) {
        this.requestId = requestId;
        this.serializedResult = serializedResult;
        this.isSuccess = isSuccess;
    }
    
    public Id<Request> getRequestId() { return requestId; }
    public String getSerializedResult() { return serializedResult; }
    public boolean isSuccess() { return isSuccess; }
}
