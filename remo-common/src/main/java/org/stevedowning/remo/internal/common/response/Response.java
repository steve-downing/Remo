package org.stevedowning.remo.internal.common.response;

import java.io.Serializable;

import org.stevedowning.remo.internal.common.request.Request;
import org.suporma.idyll.id.Id;

public class Response implements Serializable {
    private static final long serialVersionUID = -664427227833897175L;

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
