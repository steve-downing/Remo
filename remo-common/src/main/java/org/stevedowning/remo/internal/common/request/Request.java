package org.stevedowning.remo.internal.common.request;

import java.io.Serializable;

import org.suporma.idyll.id.Identifiable;

public interface Request extends Identifiable<Request>, Serializable {
    public void accept(RequestVisitor visitor);
}
