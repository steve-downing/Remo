package org.stevedowning.remo.internal.server.service;

import org.stevedowning.remo.internal.common.ClientId;

public class ClientInfo {
    private final ClientId id;
    
    public ClientInfo(ClientId id) {
        this.id = id;
    }
    
    public ClientId getId() { return id; }
}
