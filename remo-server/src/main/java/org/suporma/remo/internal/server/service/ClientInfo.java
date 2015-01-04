package org.suporma.remo.internal.server.service;

import org.suporma.remo.internal.common.ClientId;

public class ClientInfo {
    private final ClientId id;
    
    public ClientInfo(ClientId id) {
        this.id = id;
    }
    
    public ClientId getId() { return id; }
}
