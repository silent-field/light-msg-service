package com.silent.lms.mqtt.handler.ping;

import com.silent.lms.bootstrap.netty.ChannelAttributes;
import com.silent.lms.mqtt.message.pingreq.PingReq;
import com.silent.lms.mqtt.message.pingresp.PingResp;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.log4j.Log4j2;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 *
 * @author gy
 * @date 2021/5/11.
 * @version 1.0
 * @description:
 */
@Log4j2
@Singleton
@ChannelHandler.Sharable
public class PingRequestHandler extends SimpleChannelInboundHandler<PingReq> {
	@Inject
	PingRequestHandler() {
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, PingReq msg) throws Exception {
		if (log.isTraceEnabled()) {
			log.trace("收到 client {} PingReq 请求", ctx.channel().attr(ChannelAttributes.CLIENT_ID).get());
		}
		ctx.writeAndFlush(PingResp.INSTANCE);
		if (log.isTraceEnabled()) {
			log.trace("响应PingResp client {}", ctx.channel().attr(ChannelAttributes.CLIENT_ID).get());
		}
	}
}
