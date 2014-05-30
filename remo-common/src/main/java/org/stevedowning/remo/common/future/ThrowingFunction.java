package org.stevedowning.remo.common.future;

@FunctionalInterface
public interface ThrowingFunction<T, U> {
    public U apply(T input) throws Exception;
}
