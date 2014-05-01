package org.stevedowning.remo.client.service.invocation;

import java.lang.reflect.Method;

import org.stevedowning.remo.client.service.conn.ServerConnection;
import org.stevedowning.remo.common.serial.SerializationManager;

public interface MethodInvocationStrategy {
    public boolean canHandle(Method method);
    public Object handleMethodInvocation(ServerConnection conn,
            SerializationManager serializationManager, SuperserviceContext superserviceContext,
            Method method, Object[] args)
                    throws Exception;
}
