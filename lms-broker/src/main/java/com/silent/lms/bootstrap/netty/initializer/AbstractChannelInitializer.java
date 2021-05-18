package com.silent.lms.bootstrap.netty.initializer;

import com.google.common.base.Preconditions;
import com.silent.lms.annotations.NotNull;
import com.silent.lms.bootstrap.netty.ChannelAttributes;
import com.silent.lms.bootstrap.netty.ChannelDependencies;
import com.silent.lms.codec.decoder.MqttMessageDecoder;
import com.silent.lms.configuration.Listener;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

import static com.silent.lms.bootstrap.netty.ChannelHandlerNames.*;

/**
 * @author gy
 * @version 1.0
 * @date 2021/5/2
 * @description:
 */
public abstract class AbstractChannelInitializer extends ChannelInitializer<Channel> {
	private final @NotNull ChannelDependencies channelDependencies;
	private final @NotNull Listener listener;

	public AbstractChannelInitializer(
			final @NotNull ChannelDependencies channelDependencies,
			final @NotNull Listener listener) {
		this.channelDependencies = channelDependencies;
		this.listener = listener;
	}

	@Override
	protected void initChannel(final @NotNull Channel ch) throws Exception {
		Preconditions.checkNotNull(ch, "Channel不能为空");

		ch.attr(ChannelAttributes.LISTENER).set(listener);
		ch.pipeline().addLast(ALL_CHANNELS_GROUP_HANDLER, new ChannelGroupHandler(channelDependencies.getChannelGroup()));
		ch.pipeline().addLast(MQTT_MESSAGE_DECODER, new MqttMessageDecoder(channelDependencies));
		ch.pipeline().addLast(MQTT_MESSAGE_ENCODER, channelDependencies.getMqttMessageEncoder());
		ch.pipeline().addLast(MQTT_CONNECT_HANDLER, channelDependencies.getConnectHandler());
		ch.pipeline().addLast(MQTT_SUBSCRIBE_HANDLER, channelDependencies.getSubscribeHandler());

		ch.pipeline().addLast(MQTT_PINGREQ_HANDLER, channelDependencies.getPingRequestHandler());
		ch.pipeline().addLast(MQTT_UNSUBSCRIBE_HANDLER, channelDependencies.getUnsubscribeHandler());
		ch.pipeline().addLast(MQTT_DISCONNECT_HANDLER, channelDependencies.getDisconnectHandler());

		addNoConnectIdleHandler(ch);

	}

	private static final long noConnectIdleTimeout = 10 * 1000;

	protected void addNoConnectIdleHandler(final @NotNull Channel ch) {
		final long timeoutMillis = noConnectIdleTimeout;

		// 连接成功后，客户端必须在规定时间内发送第一个包Connect
		final IdleStateHandler idleStateHandler = new IdleStateHandler(timeoutMillis, 0, 0, TimeUnit.MILLISECONDS);

		ch.pipeline().addAfter(MQTT_MESSAGE_ENCODER, NEW_CONNECTION_IDLE_HANDLER, idleStateHandler);
		ch.pipeline()
				.addAfter(NEW_CONNECTION_IDLE_HANDLER, NO_CONNECT_IDLE_EVENT_HANDLER,
						channelDependencies.getNoConnectIdleHandler());
	}
}
