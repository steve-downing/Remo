package org.suporma.remo.internal.common.invocation;

import java.lang.reflect.Method;

public class MethodInvocationStrategySelector {
    private final MethodInvocationStrategy[] strategies = {
            new FutureMethodInvocationStrategy(),
            new GuavaFutureMethodInvocationStrategy(),
            new NoResultMethodInvocationStrategy(),
            new SimpleMethodInvocationStrategy()
    };

    public MethodInvocationStrategy getStrategy(Method method) {
        for (MethodInvocationStrategy strategy : strategies) {
            if (strategy.canHandle(method)) return strategy;
        }
        return new NoMethodInvocationStrategy();
    }
}
