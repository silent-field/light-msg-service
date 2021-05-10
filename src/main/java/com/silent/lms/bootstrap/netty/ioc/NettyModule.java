package com.silent.lms.bootstrap.netty.ioc;

import com.silent.lms.bootstrap.netty.ChannelInitializerFactory;
import com.silent.lms.bootstrap.netty.ChannelInitializerFactoryImpl;
import com.silent.lms.ioc.SingletonModule;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * @author gy
 * @version 1.0
 * @date 2021/5/2
 * @description:
 */
public class NettyModule extends SingletonModule {
    public NettyModule() {
        super(NettyModule.class);
    }

    @Override
    protected void configure() {
        bind(ChannelGroup.class).toInstance(new DefaultChannelGroup(GlobalEventExecutor.INSTANCE));
        bind(NettyConfigurator.class).toInstance(new NettyConfigurator());
        bind(ChannelInitializerFactory.class).to(ChannelInitializerFactoryImpl.class);
    }
}
