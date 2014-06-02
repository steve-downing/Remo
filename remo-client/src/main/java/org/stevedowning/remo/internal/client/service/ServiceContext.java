package org.stevedowning.remo.internal.client.service;

public class ServiceContext {
    private final ServiceContext parentContext;
    
    private ServiceContext(ServiceContext parentContext) {
        this.parentContext = parentContext;
    }
    
    public static ServiceContext getBaseContext() {
        return new ServiceContext(null);
    }
    
    public ServiceContext createSubcontext() { return new ServiceContext(this); }
    
    public ServiceContext getParentContext() { return parentContext; }
    public boolean hasParentContext() { return parentContext != null; }
}
