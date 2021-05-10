package com.silent.lms.mqtt.handler.connect;

import com.silent.lms.annotations.NotNull;
import com.silent.lms.mqtt.handler.MessageHandler;
import com.silent.lms.mqtt.handler.connack.MqttConnacker;
import com.silent.lms.mqtt.message.connack.Connack;
import com.silent.lms.mqtt.message.connack.MqttConnAckReturnCode;
import com.silent.lms.mqtt.message.connect.Connect;
import io.netty.channel.*;
import lombok.extern.log4j.Log4j2;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

/**
 * @author gy
 * @version 1.0
 * @date 2021/5/6.
 * @description:
 */
@Log4j2
@Singleton
@ChannelHandler.Sharable
public class ConnectHandler extends SimpleChannelInboundHandler<Connect> implements MessageHandler<Connect> {
	private final @NotNull MqttConnacker mqttConnacker;

	@Inject
	public ConnectHandler(
			final @NotNull MqttConnacker mqttConnacker) {
		this.mqttConnacker = mqttConnacker;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Connect connect) throws Exception {
		defaultNotSetValues(connect);

		// TODO 检查clientID合法性，检查will合法性

		// TODO 鉴权auth

		// TODO 持久化

		//
		connectSuccessful(ctx,connect);
	}

	private void connectSuccessful(final @NotNull ChannelHandlerContext ctx,
								   final @NotNull Connect connect){
		mqttConnacker.connackSuccess(ctx, new Connack(MqttConnAckReturnCode.ACCEPTED, false));
	}

	@Override
	public void defaultNotSetValues(@NotNull Connect connect) {

	}
}
