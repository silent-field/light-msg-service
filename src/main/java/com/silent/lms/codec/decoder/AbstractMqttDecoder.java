package com.silent.lms.codec.decoder;


import com.silent.lms.annotations.NotNull;
import com.silent.lms.mqtt.handler.disconnect.MqttServerDisconnector;
import com.silent.lms.mqtt.message.Message;
import com.silent.lms.mqtt.message.MessageType;
import com.silent.lms.mqtt.reason.MqttDisconnectReasonCode;
import io.netty.channel.Channel;

/**
 * @author gy
 * @version 1.0
 * @date 2021/5/8.
 * @description:
 */
public abstract class AbstractMqttDecoder<T extends Message> extends MqttDecoder<T> {
	protected final @NotNull MqttServerDisconnector disconnector;

	protected AbstractMqttDecoder(final @NotNull MqttServerDisconnector disconnector) {
		this.disconnector = disconnector;
	}

	/**
	 * fixed header不合法，需要断开连接
	 *
	 * @param channel
	 * @param messageType
	 */
	protected void disconnectByInvalidFixedHeader(final @NotNull Channel channel, final @NotNull MessageType messageType) {
		disconnector.disconnect(channel,
				"client (IP: {})发送" + messageType.name() + "包fixed header不合法",
				"发送" + messageType.name() + "包fixed header不合法",
				MqttDisconnectReasonCode.MALFORMED_PACKET,
				String.format("%s 包fixed header不合法", messageType.name()), false);
	}

	/**
	 * 协议包没有消息标识，需要断开连接
	 *
	 * @param channel     the channel of the mqtt client
	 * @param messageType the type of the message
	 */
	protected void disconnectByNoMessageId(final @NotNull Channel channel, final @NotNull MessageType messageType) {
		disconnector.disconnect(channel,
				"client (IP: {}) 发送" + messageType.name() + " 包没有消息标识",
				"发送 " + messageType.name() + " 包没有消息标识",
				MqttDisconnectReasonCode.PROTOCOL_ERROR,
				String.format("%s 包没有消息标识", messageType.name()), false);
	}
}
