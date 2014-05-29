package org.stevedowning.remo.common.service;

import java.io.Serializable;
import java.lang.reflect.Method;

import org.stevedowning.commons.idyll.Id;

public class ServiceMethodId implements Id<ServiceMethod>, Serializable {
    private static final long serialVersionUID = 8783399404127942634L;

    private final String methodStr;
    
    public ServiceMethodId(Method m) {
        this.methodStr = m.toGenericString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((methodStr == null) ? 0 : methodStr.hashCode());
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
        ServiceMethodId other = (ServiceMethodId) obj;
        if (methodStr == null) {
            if (other.methodStr != null)
                return false;
        } else if (!methodStr.equals(other.methodStr))
            return false;
        return true;
    }
}
