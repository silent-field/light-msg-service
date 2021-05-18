package com.silent.lms.bootstrap.netty;

import com.silent.lms.annotations.NotNull;
import com.silent.lms.bootstrap.netty.initializer.AbstractChannelInitializer;
import com.silent.lms.configuration.Listener;

/**
 * @author gy
 * @version 1.0
 * @date 2021/5/2
 * @description:
 */
public interface ChannelInitializerFactory {
    @NotNull
    AbstractChannelInitializer getChannelInitializer(final @NotNull Listener listener);
}
