package org.stevedowning.remo.common.request;

import java.util.LinkedList;
import java.util.List;

import org.stevedowning.commons.idyll.Id;
import org.stevedowning.commons.idyll.Identifiable;
import org.stevedowning.commons.idyll.datastructures.IdMap;
import org.stevedowning.commons.idyll.datastructures.LinkedHashIdMap;

public class ConnectionRequestBatch implements Identifiable<ConnectionRequestBatch> {
    private final Id<ConnectionRequestBatch> id;
    private final IdMap<ConnectionRequest> requests;
    
    public ConnectionRequestBatch(Id<ConnectionRequestBatch> id) {
        this.id = id;
        this.requests = new LinkedHashIdMap<ConnectionRequest>();
    }
    
    public Id<ConnectionRequestBatch> getId() { return id; }
    
    public synchronized void add(ConnectionRequest request) { requests.add(request); }
    public synchronized void remove(Id<ConnectionRequest> requestId) { requests.remove(requestId); }
    public synchronized List<ConnectionRequest> getRequests() {
        return new LinkedList<ConnectionRequest>(requests.values());
    }
    public int size() { return requests.size(); }
}
