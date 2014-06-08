package org.stevedowning.remo.internal.common.request;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.stevedowning.commons.idyll.Id;
import org.stevedowning.commons.idyll.Identifiable;
import org.stevedowning.commons.idyll.datastructures.IdMap;
import org.stevedowning.commons.idyll.datastructures.LinkedHashIdMap;

public class RequestBatch implements Identifiable<RequestBatch>, Iterable<Request>, Serializable {
    private static final long serialVersionUID = 7435696533101840173L;

    private final Id<RequestBatch> id;
    private final IdMap<Request> requests;
    
    public RequestBatch(Id<RequestBatch> id) {
        this.id = id;
        this.requests = new LinkedHashIdMap<Request>();
    }
    
    public Id<RequestBatch> getId() { return id; }
    
    public synchronized void add(Request request) { requests.add(request); }
    public synchronized void remove(Id<Request> requestId) { requests.remove(requestId); }
    public synchronized List<Request> getRequests() {
        return new LinkedList<Request>(requests.values());
    }
    public int size() { return requests.size(); }
    public Iterator<Request> iterator() { return requests.iterator(); }
}
