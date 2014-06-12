package org.stevedowning.remo.internal.common.invocation;

import java.lang.reflect.Method;

public class MethodInvocationStrategySelector {
    private final MethodInvocationStrategy[] strategies = {
            new SimpleMethodInvocationStrategy()
    };

    public MethodInvocationStrategy getStrategy(Method method) {
        for (MethodInvocationStrategy strategy : strategies) {
            if (strategy.canHandle(method)) return strategy;
        }
        return new NoMethodInvocationStrategy();
    }
}
