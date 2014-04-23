package org.stevedowning.remo.common.response;

import java.util.HashMap;
import java.util.Map;

import org.stevedowning.commons.idyll.Id;
import org.stevedowning.remo.common.request.Request;
import org.stevedowning.remo.common.request.RequestBatch;

public class ResponseBatch {
    private final Id<RequestBatch> requestBatchId;
    private final Map<Id<Request>, Response> responses;
    
    public ResponseBatch(Id<RequestBatch> requestBatchId) {
        this.requestBatchId = requestBatchId;
        this.responses = new HashMap<Id<Request>, Response>();
    }
    
    public Id<RequestBatch> getRequestBatchId() { return requestBatchId; }
    
    public synchronized void addResponse(Response response) {
        responses.put(response.getRequestId(), response);
    }
}
