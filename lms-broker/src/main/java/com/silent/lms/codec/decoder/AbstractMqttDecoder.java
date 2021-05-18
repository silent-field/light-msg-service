package com.silent.lms.codec.decoder;


import com.silent.lms.annotations.NotNull;
import com.silent.lms.annotations.Nullable;
import com.silent.lms.mqtt.handler.disconnect.MqttServerDisconnector;
import com.silent.lms.mqtt.message.Message;
import com.silent.lms.mqtt.message.MessageType;
import com.silent.lms.mqtt.reason.MqttDisconnectReasonCode;
import com.silent.lms.util.Strings;
import com.silent.lms.util.Topics;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

/**
 * @author gy
 * @version 1.0
 * @date 2021/5/8.
 * @description:
 */
public abstract class AbstractMqttDecoder<T extends Message> extends MqttDecoder<T> {
	protected static final int DISCONNECTED = -1;

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

	/**
	 * decode一个UTF-8编码的 topic 字符串长度，并且验证可读长度
	 *
	 * @param channel     the channel of the mqtt client
	 * @param buf         the encoded ByteBuf of the message
	 * @param key         the name of the property, eg. 'will topic' or 'topic'
	 * @param messageType the type of the message
	 * @return the length of the string or -1 for malformed packet
	 */
	protected int decodeUTF8StringLength(final @NotNull Channel channel, final @NotNull ByteBuf buf, final @NotNull String key, final @NotNull MessageType messageType) {
		final int utf8StringLength;

		if (buf.readableBytes() < 2 || (buf.readableBytes() < (utf8StringLength = buf.readUnsignedShort()))) {
			disconnector.disconnect(channel,
					"client (IP: {}) 发送的" + messageType.name() + "消息中 UTF-8 字符串长度不正确. '" + key,
					messageType.name() + " 消息中 UTF-8 字符串长度不正确. " + key,
					MqttDisconnectReasonCode.MALFORMED_PACKET,
					"消息中 UTF-8 字符串长度不正确. ", false);
			return DISCONNECTED;

		}

		return utf8StringLength;
	}

	@Nullable
	protected String decodeUTF8Topic(final @NotNull Channel channel, final @NotNull ByteBuf buf, final int utf8StringLength, boolean validateUTF8, final @NotNull String key, final @NotNull MessageType messageType) {
		final String utf8String = Strings.getValidatedPrefixedString(buf, utf8StringLength, validateUTF8);
		if (utf8String == null) {
			disconnector.disconnect(channel,
					"client (IP: {}) 发送的" + messageType.name() + "消息中'" + key + "'不是UTF-8格式",
					"发送的" + messageType.name() + "消息中'" + key + "'不是UTF-8格式",
					MqttDisconnectReasonCode.MALFORMED_PACKET,
					messageType.name() + "消息中'" + key + "'不是UTF-8格式", false);
		}
		return utf8String;
	}

	protected boolean topicInvalid(final @NotNull Channel channel, final @NotNull String topicName, final @NotNull MessageType messageType) {
		if (Topics.containsWildcard(topicName)) {
			disconnector.disconnect(channel,
					"client (IP: {}) 发送的" + messageType.name() + "topic中包含通配符(#/+)",
					"发送的" + messageType.name() + "包含通配符(#/+), topic: " + topicName,
					MqttDisconnectReasonCode.TOPIC_NAME_INVALID,
					"包含通配符(#/+)",false);

			return true;
		}
		return false;
	}
}
