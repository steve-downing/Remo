package org.stevedowning.remo.internal.common.request;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.stevedowning.remo.internal.common.ClientId;
import org.suporma.idyll.id.Id;
import org.suporma.idyll.id.Identifiable;
import org.suporma.idyll.util.IdMap;
import org.suporma.idyll.util.LinkedHashIdMap;

public class RequestBatch implements Identifiable<RequestBatch>, Iterable<Request>, Serializable {
    private static final long serialVersionUID = 7435696533101840173L;

    private final ClientId clientId;
    private final Id<RequestBatch> batchId;
    private final IdMap<Request> requests;
    
    public RequestBatch(ClientId clientId, Id<RequestBatch> batchId) {
        this.clientId = clientId;
        this.batchId = batchId;
        this.requests = new LinkedHashIdMap<Request>();
    }
    
    public ClientId getClientId() { return clientId; }
    public Id<RequestBatch> getId() { return batchId; }
    
    public synchronized void add(Request request) { requests.add(request); }
    public synchronized void remove(Id<Request> requestId) { requests.remove(requestId); }
    public synchronized List<Request> getRequests() {
        return new LinkedList<Request>(requests.values());
    }
    public int size() { return requests.size(); }
    public Iterator<Request> iterator() { return requests.iterator(); }
}
