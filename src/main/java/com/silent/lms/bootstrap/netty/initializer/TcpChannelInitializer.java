package com.silent.lms.bootstrap.netty.initializer;

import com.silent.lms.annotations.NotNull;
import com.silent.lms.bootstrap.netty.ChannelDependencies;
import com.silent.lms.configuration.TcpListener;
import io.netty.channel.Channel;

import javax.inject.Provider;

/**
 * @author gy
 * @version 1.0
 * @date 2021/5/4
 * @description:
 */
public class TcpChannelInitializer extends AbstractChannelInitializer {
    public TcpChannelInitializer(@NotNull final ChannelDependencies channelDependencies,
                                 @NotNull final TcpListener tcpListener) {
        super(channelDependencies, tcpListener);
    }
}
