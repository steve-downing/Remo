package org.stevedowning.remo.common.response;

import java.util.HashMap;
import java.util.Map;

import org.stevedowning.commons.idyll.Id;
import org.stevedowning.remo.common.request.ConnectionRequest;
import org.stevedowning.remo.common.request.ConnectionRequestBatch;

public class ConnectionResponseBatch {
    private final Id<ConnectionRequestBatch> requestBatchId;
    private final int numExpectedResults;
    private final Map<Id<ConnectionRequest>, ConnectionResponse> responses;
    
    public ConnectionResponseBatch(Id<ConnectionRequestBatch> requestBatchId, int numExpectedResults) {
        this.requestBatchId = requestBatchId;
        this.numExpectedResults = numExpectedResults;
        this.responses = new HashMap<Id<ConnectionRequest>, ConnectionResponse>();
    }
    
    public static ConnectionResponseBatch forRequestBatch(ConnectionRequestBatch requestBatch) {
        return new ConnectionResponseBatch(requestBatch.getId(), requestBatch.size());
    }
    
    public Id<ConnectionRequestBatch> getRequestBatchId() { return requestBatchId; }
    
    public synchronized void addResponse(ConnectionResponse response) {
        responses.put(response.getRequestId(), response);
    }
    
    public synchronized boolean isDone() { return responses.size() == numExpectedResults; }
    public ConnectionResponse get(Id<ConnectionRequest> requestId) {
        return responses.get(requestId);
    }
}
