package com.silent.lms.mqtt.handler.connect;

import com.silent.lms.annotations.NotNull;
import com.silent.lms.mqtt.handler.connack.MqttConnacker;
import com.silent.lms.mqtt.message.connack.Connack;
import com.silent.lms.mqtt.message.connect.Connect;
import com.silent.lms.mqtt.reason.MqttConnAckReasonCode;
import com.silent.lms.util.ChannelUtils;
import com.silent.lms.util.Topics;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.log4j.Log4j2;

import javax.inject.Inject;
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
public class ConnectHandler extends SimpleChannelInboundHandler<Connect> {
	private final @NotNull MqttConnacker mqttConnacker;

	@Inject
	public ConnectHandler(
			final @NotNull MqttConnacker mqttConnacker) {
		this.mqttConnacker = mqttConnacker;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Connect connect) throws Exception {
		// 当前实现不检查clientID长度、只包含大写字母，小写字母和数字等合法性

		//  检查will合法性

		// TODO 鉴权auth

		// TODO 持久化

		// TODO 添加心跳检测

		//
		log.info("Client IP {} 连接完成", ChannelUtils.getChannelIP(ctx.channel()).or("UNKNOWN"));
		connectSuccessful(ctx, connect);
	}

	private void connectSuccessful(final @NotNull ChannelHandlerContext ctx,
								   final @NotNull Connect connect) {
		mqttConnacker.connackSuccess(ctx, new Connack(MqttConnAckReasonCode.ACCEPTED, false));
	}

	private boolean checkWill(final @NotNull ChannelHandlerContext ctx, final @NotNull Connect msg) {
		if (msg.getWill() != null) {
			// 遗嘱消息topic不允许通配符
			if (Topics.containsWildcard(msg.getWill().getTopic())) {
				mqttConnacker.connackError(
						ctx.channel(),
						"client (IP: {}) 发送的CONNECT消息中will topic使用了通配符(# or +)",
						"发送的CONNECT消息中will topic使用了通配符(# or +)",
						MqttConnAckReasonCode.TOPIC_NAME_INVALID);
				return false;

			}
		}
		return true;
	}
}
