package com.silent.lms.ioc;

import com.google.inject.AbstractModule;
import lombok.extern.log4j.Log4j2;

/**
 * @author gy
 * @version 1.0
 * @date 2021/5/2
 * @description:
 */
@Log4j2
public abstract  class SingletonModule<T> extends AbstractModule {
    protected T key;

    public SingletonModule(final T key) {
        this.key = key;
    }

    @Override
    public boolean equals(final Object obj) {
        return obj instanceof SingletonModule
                && ((SingletonModule) obj).key.equals(key);
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    @Override
    public String toString() {
        return getClass().getName() + "(key=" + key.toString() + ")";
    }
}
