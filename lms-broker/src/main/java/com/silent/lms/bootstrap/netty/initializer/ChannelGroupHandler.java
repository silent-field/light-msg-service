package com.silent.lms.bootstrap.netty.initializer;

import com.google.common.base.Preconditions;
import com.silent.lms.annotations.NotNull;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;

/**
 * @author gy
 * @version 1.0
 * @date 2021/5/3
 * @description:
 */
public class ChannelGroupHandler extends ChannelInboundHandlerAdapter {
    @NotNull
    private final ChannelGroup channelGroup;

    public ChannelGroupHandler(final @NotNull ChannelGroup channelGroup) {
        Preconditions.checkNotNull(channelGroup, "ChannelGroup不能为空");
        this.channelGroup = channelGroup;
    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) {
        channelGroup.add(ctx.channel());
        ctx.fireChannelActive();
        ctx.pipeline().remove(this);
    }
}
