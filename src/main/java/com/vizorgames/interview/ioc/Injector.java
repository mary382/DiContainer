package com.vizorgames.interview.ioc;

public interface Injector
{
    <T> Provider<T> getProvider(Class<T> type);

    <T> void bind(Class<T> base, Class<? extends T> impl);

    <T> void bindSingleton(Class<T> base, Class<? extends T> impl);
}
