package org.stevedowning.remo;

@FunctionalInterface
public interface ThrowingFunction<T, U> {
    public U apply(T input) throws Throwable;
}
