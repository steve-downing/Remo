package org.stevedowning.remo.internal.common.request;

import java.io.Serializable;

import org.stevedowning.commons.idyll.Identifiable;

public interface Request extends Identifiable<Request>, Serializable {
    public void accept(RequestVisitor visitor);
}
