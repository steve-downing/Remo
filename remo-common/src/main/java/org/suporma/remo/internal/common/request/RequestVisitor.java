package org.suporma.remo.internal.common.request;

public interface RequestVisitor {
    void visit(InvocationRequest invocationRequest);
    void visit(CancellationRequest cancellationRequest);
}
