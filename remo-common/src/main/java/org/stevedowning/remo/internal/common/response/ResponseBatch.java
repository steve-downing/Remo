package org.stevedowning.remo.internal.common.response;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.stevedowning.commons.idyll.Id;
import org.stevedowning.remo.internal.common.request.Request;
import org.stevedowning.remo.internal.common.request.RequestBatch;

public class ResponseBatch implements Iterable<Response>, Serializable {
    private static final long serialVersionUID = 8764303453222320772L;

    private final Id<RequestBatch> requestBatchId;
    private final int numExpectedResults;
    private final Map<Id<Request>, Response> responses;
    
    public ResponseBatch(Id<RequestBatch> requestBatchId, int numExpectedResults) {
        this.requestBatchId = requestBatchId;
        this.numExpectedResults = numExpectedResults;
        this.responses = new HashMap<>();
    }
    
    public static ResponseBatch forRequestBatch(RequestBatch requestBatch) {
        return new ResponseBatch(requestBatch.getId(), requestBatch.size());
    }
    
    public Id<RequestBatch> getRequestBatchId() { return requestBatchId; }
    
    public synchronized void addResponse(Response response) {
        responses.put(response.getRequestId(), response);
    }
    
    public synchronized boolean isDone() { return responses.size() == numExpectedResults; }
    public Response get(Id<Request> requestId) { return responses.get(requestId); }
    public Iterator<Response> iterator() { return responses.values().iterator(); }
}
