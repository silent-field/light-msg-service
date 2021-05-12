package com.silent.lms.mqtt.handler.disconnect;

import com.silent.lms.annotations.NotNull;
import com.silent.lms.bootstrap.netty.ChannelAttributes;
import com.silent.lms.logging.EventLog;
import com.silent.lms.mqtt.message.disconnect.Disconnect;
import com.silent.lms.persistence.SubscribeHolder;
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
public class DisconnectHandler extends SimpleChannelInboundHandler<Disconnect> {
	@NotNull
	private final  EventLog eventLog;

	@Inject
	public DisconnectHandler(final @NotNull EventLog eventLog) {
		this.eventLog = eventLog;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Disconnect msg) throws Exception {
		eventLog.clientDisconnected(ctx.channel());

		ctx.channel().close();
	}

	@Override
	public void channelInactive(final ChannelHandlerContext ctx) throws Exception {
		SubscribeHolder subscribeHolder = SubscribeHolder.instance;

		subscribeHolder.removeSubscriber(ctx.channel());
	}
}
