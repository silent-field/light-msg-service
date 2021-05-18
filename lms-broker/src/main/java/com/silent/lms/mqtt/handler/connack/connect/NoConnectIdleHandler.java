package com.silent.lms.mqtt.handler.connack.connect;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.silent.lms.annotations.NotNull;
import com.silent.lms.logging.EventLog;
import com.silent.lms.mqtt.message.connect.Connect;
import com.silent.lms.util.ChannelUtils;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.log4j.Log4j2;

import java.util.NoSuchElementException;

import static com.silent.lms.bootstrap.netty.ChannelHandlerNames.NEW_CONNECTION_IDLE_HANDLER;

@Log4j2
@Singleton
@ChannelHandler.Sharable
public class NoConnectIdleHandler extends ChannelInboundHandlerAdapter {
    private final @NotNull EventLog eventLog;

    @Inject
    public NoConnectIdleHandler(final @NotNull EventLog eventLog) {
        this.eventLog = eventLog;
    }

    @Override
    public void channelRead(final @NotNull ChannelHandlerContext ctx, final @NotNull Object msg) {
        // 第一个包必须是Connect，如果在规定时间内收到Connect包，则移除超时监听handler
        if (msg instanceof Connect) {
            try {
                ctx.pipeline().remove(NEW_CONNECTION_IDLE_HANDLER);
                ctx.pipeline().remove(this);
            } catch (final NoSuchElementException ex) {
                log.trace("Not able to remove no connect idle handler");
            }
        }
        ctx.fireChannelRead(msg);
    }

    @Override
    public void userEventTriggered(final @NotNull ChannelHandlerContext ctx, final @NotNull Object evt) {
        // 在规定的时间内未能收到Connect包，关闭连接
        if (evt instanceof IdleStateEvent) {
            if (((IdleStateEvent) evt).state() == IdleState.READER_IDLE) {
                if (log.isDebugEnabled()) {
                    log.debug("Client IP {} 超时未发送Connect报文", ChannelUtils.getChannelIP(ctx.channel()).or("UNKNOWN"));
                }
                eventLog.clientWasDisconnected(ctx.channel(), "超时未发送Connect报文");
                ctx.close();
                return;
            }
        }
        ctx.fireUserEventTriggered(evt);
    }
}