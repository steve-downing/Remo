package org.suporma.remo.internal.common;

import java.io.Serializable;
import java.util.Date;
import java.util.Random;

import org.suporma.idyll.id.Id;

public class ClientId implements Id<Object>, Serializable {
    private static final long serialVersionUID = 1829355624289827151L;

    private long timestamp;
    private long secret;
    
    private ClientId() {}
    
    public static ClientId generate() {
        ClientId id = new ClientId();
        id.timestamp = new Date().getTime();
        id.secret = new Random().nextLong();
        return id;
    }
    
    public String toString() {
        return timestamp + "-" + secret;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (secret ^ (secret >>> 32));
        result = prime * result + (int) (timestamp ^ (timestamp >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ClientId other = (ClientId) obj;
        if (secret != other.secret)
            return false;
        if (timestamp != other.timestamp)
            return false;
        return true;
    }
}
