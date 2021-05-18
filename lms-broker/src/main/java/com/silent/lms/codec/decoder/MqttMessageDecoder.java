package com.silent.lms.codec.decoder;

import com.silent.lms.annotations.NotNull;
import com.silent.lms.bootstrap.netty.ChannelAttributes;
import com.silent.lms.bootstrap.netty.ChannelDependencies;
import com.silent.lms.codec.MqttProtocolConstants;
import com.silent.lms.mqtt.handler.disconnect.MqttServerDisconnector;
import com.silent.lms.mqtt.message.Message;
import com.silent.lms.mqtt.message.MessageType;
import com.silent.lms.mqtt.message.ProtocolVersion;
import com.silent.lms.mqtt.reason.MqttDisconnectReasonCode;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

import static com.silent.lms.mqtt.message.MessageType.CONNECT;

/**
 * @author gy
 * @version 1.0
 * @date 2021/5/3
 * @description: <p>
 * Fixed header         固定报头，所有控制报文都包含
 * Variable header      可变报头，部分控制报文包含
 * Payload              有效载荷，部分控制报文包含
 * <p>
 * 固定报头 Fixed header
 * byte 1       MQTT控制报文的类型(7~4)     用于指定控制报文类型的标志位(3~0)
 * byte 2...    剩余长度，剩余长度（Remaining Length）表示当前报文剩余部分的字节数，包括可变报头和负载的数据。
 * 最多4个字节，每个字节最高位表示后续是否还有剩余长度字节，4个字节(4*7bit)最大可表示256MB
 * <p>
 * 可变报头 Variable header
 * 很多控制报文的可变报头部分包含一个两字节的报文标识符字段。报文标识符 Packet Identifier
 * 这些报文是PUBLISH（QoS > 0时）， PUBACK，PUBREC，PUBREL，PUBCOMP，SUBSCRIBE, SUBACK，UNSUBSCRIBE，UNSUBACK。
 * PUBLISH      需要（如果QoS > 0）
 * PUBACK       需要
 * PUBREC       需要
 * PUBREL       需要
 * PUBCOMP      需要
 * SUBSCRIBE    需要
 * SUBACK       需要
 * UNSUBSCRIBE  需要
 * UNSUBACK     需要
 * <p>
 * 有效载荷 Payload
 * CONNECT      需要
 * PUBLISH      可选
 * SUBSCRIBE    需要
 * SUBACK       需要
 * UNSUBSCRIBE  需要
 */
public class MqttMessageDecoder extends ByteToMessageDecoder {
	private static final int MIN_FIXED_HEADER_LENGTH = 2;

	private static final int MAX_REMAINING_LENGTH_MULTIPLIER = 0x80 * 0x80 * 0x80;
	private static final int NOT_ENOUGH_REMAINING_LENGTH_BYTES_READABLE = -2;
	private static final int MALFORMED_REMAINING_LENGTH = -1;

	private final @NotNull MqttDecoders mqttDecoders;
	private final @NotNull MqttServerDisconnector mqttServerDisconnector;

	public MqttMessageDecoder(final @NotNull ChannelDependencies channelDependencies) {
		this.mqttDecoders = channelDependencies.getMqttDecoders();
		this.mqttServerDisconnector = channelDependencies.getMqttServerDisconnector();
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> out) throws Exception {
		final int readableBytes = buf.readableBytes();
		if (readableBytes < MIN_FIXED_HEADER_LENGTH) {
			return;
		}

		buf.markReaderIndex();

		final byte fixedHeader = buf.readByte();

		final int remainingLength = calculateRemainingLength(buf);

		if (remainingLength == NOT_ENOUGH_REMAINING_LENGTH_BYTES_READABLE) {
			buf.resetReaderIndex();
			return;
		}

		// 关闭连接
		Channel channel = ctx.channel();
		if (remainingLength == MALFORMED_REMAINING_LENGTH) {
			mqttServerDisconnector.disconnect(channel,
					"client (IP: {})剩余长度过长",
					"剩余长度过长",
					MqttDisconnectReasonCode.MALFORMED_PACKET,
					"剩余长度过长",
					false);
			buf.clear();
			return;
		}

		if (buf.readableBytes() < remainingLength) {
			buf.resetReaderIndex();
			return;
		}

		int fixedHeaderSize = getFixedHeaderSize(remainingLength);
		if (remainingLength + fixedHeaderSize > MqttProtocolConstants.MAXIMUM_PACKET_SIZE_LIMIT) {
			mqttServerDisconnector.disconnect(channel, "client (IP: {}) 超过最大包限制",
					"超过最大包限制", MqttDisconnectReasonCode.PACKET_TOO_LARGE, "超过最大包限制", false);
			buf.clear();
			return;
		}

		final ProtocolVersion protocolVersion = channel.attr(ChannelAttributes.MQTT_VERSION).get();

		final Message message;
		final ByteBuf messageBuffer = buf.readSlice(remainingLength);
		buf.markReaderIndex();

		final MessageType messageType = getMessageType(fixedHeader);

		if (protocolVersion == null && messageType != CONNECT) {
			mqttServerDisconnector.logAndClose(channel,
					"client (IP: {})在Connect之前发送了其他包",
					"在Connect之前发送了其他包");
			buf.clear();
			return;
		}

		if (protocolVersion != null && messageType == CONNECT) {
			mqttServerDisconnector.logAndClose(channel,
					"client (IP: {})发送了第二次Connect包",
					"发送了第二次Connect包");
			buf.clear();
			return;
		}

		final MqttDecoder<?> decoder = mqttDecoders.getDecoder(messageType);

		if (decoder == null) {
			mqttServerDisconnector.disconnect(channel,
					"client (IP: {}) 协议类型不支持，fixedHeader : " + fixedHeader,
					"协议类型不支持",
					MqttDisconnectReasonCode.PROTOCOL_ERROR,
					"协议类型不支持", false);
			buf.clear();
			return;
		}

		message = decoder.decode(channel, messageBuffer, fixedHeader);

		if (message == null) {
			buf.clear();
			return;
		}

		out.add(message);
	}

	private int getFixedHeaderSize(final int remainingLength) {

		//  1 byte 固定报头 + 1~4 byte 剩余长度
		int remainingLengthSize = 2;

		if (remainingLength > 127) {
			remainingLengthSize++;
		}
		if (remainingLength > 16383) {
			remainingLengthSize++;
		}
		if (remainingLength > 2097151) {
			remainingLengthSize++;
		}
		return remainingLengthSize;
	}

	/**
	 * 计算剩余长度
	 *
	 * @param buf
	 * @return
	 */
	private int calculateRemainingLength(ByteBuf buf) {
		int remainingLength = 0;
		int multiplier = 1;
		byte encodedByte;

		do {
			// 超过4个字节，意味着这个报文非法
			if (multiplier > MAX_REMAINING_LENGTH_MULTIPLIER) {
				buf.skipBytes(buf.readableBytes());
				return MALFORMED_REMAINING_LENGTH;
			}

			// 还未能获取所有剩余长度字节
			if (!buf.isReadable()) {
				return NOT_ENOUGH_REMAINING_LENGTH_BYTES_READABLE;
			}

			encodedByte = buf.readByte();
			// 取字节低7位计算剩余长度
			remainingLength += ((encodedByte & (byte) 0x7f) * multiplier);
			multiplier *= 0x80;

			// 直至读取到最高位不为1的字节
		} while ((encodedByte & 0x80) != 0);

		return remainingLength;
	}

	private MessageType getMessageType(final byte fixedHeader) {
		// 高4位表示报文类型
		return MessageType.getByCode((fixedHeader & 0b1111_0000) >> 4);
	}
}
