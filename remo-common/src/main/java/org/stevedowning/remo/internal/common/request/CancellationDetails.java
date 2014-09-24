package org.stevedowning.remo.internal.common.request;

import java.io.Serializable;

import org.stevedowning.commons.idyll.Id;
import org.stevedowning.remo.internal.common.ClientId;

public class CancellationDetails implements Serializable {
    private static final long serialVersionUID = 8809371248190740444L;

    private final Id<Request> cancellationTargetId;
    private final ClientId clientId;
    
    public CancellationDetails(ClientId clientId, Id<Request> cancellationTargetId) {
        this.clientId = clientId;
        this.cancellationTargetId = cancellationTargetId;
    }
    
    public ClientId getClientId() { return clientId; }
    public Id<Request> getCancellationTargetId() { return cancellationTargetId; }

    public String toString() {
        return "CancellationDetails [cancellationTargetId="
                + cancellationTargetId + ", clientId=" + clientId + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime
                * result
                + ((cancellationTargetId == null) ? 0 : cancellationTargetId
                        .hashCode());
        result = prime * result
                + ((clientId == null) ? 0 : clientId.hashCode());
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
        CancellationDetails other = (CancellationDetails) obj;
        if (cancellationTargetId == null) {
            if (other.cancellationTargetId != null)
                return false;
        } else if (!cancellationTargetId.equals(other.cancellationTargetId))
            return false;
        if (clientId == null) {
            if (other.clientId != null)
                return false;
        } else if (!clientId.equals(other.clientId))
            return false;
        return true;
    }
}
