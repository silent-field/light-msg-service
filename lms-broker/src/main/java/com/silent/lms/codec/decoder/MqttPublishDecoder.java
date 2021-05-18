package com.silent.lms.codec.decoder;

import com.google.inject.Inject;
import com.silent.lms.annotations.NotNull;
import com.silent.lms.annotations.Nullable;
import com.silent.lms.configuration.LmsServerId;
import com.silent.lms.mqtt.handler.disconnect.MqttServerDisconnector;
import com.silent.lms.mqtt.message.MessageType;
import com.silent.lms.mqtt.message.QoS;
import com.silent.lms.mqtt.message.publish.Publish;
import com.silent.lms.mqtt.reason.MqttDisconnectReasonCode;
import com.silent.lms.util.Bytes;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

import javax.inject.Singleton;

/**
 *
 * @author gy
 * @date 2021/5/12.
 * @version 1.0
 * @description:
 * <p>
 *  固定报头 Fixed header
 *  byte 1      0011(报文类型：PUBLISH)
 *				DUP		QoS-H		QoS-L		RETAIN
 *	重发标志 DUP	位置：第1个字节，第3位
 *		如果DUP标志被设置为0，表示这是客户端或服务端第一次请求发送这个PUBLISH报文。如果DUP标志被设置为1，表示这可能是一个早前报文请求的重发。
 *		客户端或服务端请求重发一个PUBLISH报文时，必须将DUP标志设置为1 [MQTT-3.3.1.-1].。对于QoS 0的消息，DUP标志必须设置为0 [MQTT-3.3.1-2]。
 *		服务端发送PUBLISH报文给订阅者时，收到（入站）的PUBLISH报文的DUP标志的值不会被传播。
 *		发送（出站）的PUBLISH报文与收到（入站）的PUBLISH报文中的DUP标志是独立设置的，它的值必须单独的根据发送（出站）的PUBLISH报文是否是一个重发来确定 [MQTT-3.3.1-3]。
 *	服务质量等级 QoS	位置：第1个字节，第2-1位。
 *		PUBLISH报文不能将QoS所有的位设置为1。如果服务端或客户端收到QoS所有位都为1的PUBLISH报文，它必须关闭网络连接 [MQTT-3.3.1-4]。
 *	保留标志 RETAIN	位置：第1个字节，第0位。
 *		如果客户端发给服务端的PUBLISH报文的保留（RETAIN）标志被设置为1，服务端必须存储这个应用消息和它的服务质量等级（QoS），
 *		以便它可以被分发给未来的主题名匹配的订阅者 [MQTT-3.3.1-5]。
 *		一个新的订阅建立时，对每个匹配的主题名，如果存在最近保留的消息，它必须被发送给这个订阅者 [MQTT-3.3.1-6]。
 *		如果服务端收到一条保留（RETAIN）标志为1的QoS 0消息，它必须丢弃之前为那个主题保留的任何消息。
 *		它应该将这个新的QoS 0消息当作那个主题的新保留消息，但是任何时候都可以选择丢弃它 — 如果这种情况发生了，那个主题将没有保留消息 [MQTT-3.3.1-7]。
 *  byte 2...   剩余长度等于可变报头加上有效载荷的长度
 * <p>
 *  可变报头 Variable header
 *  可变报头按顺序包含主题名和报文标识符。
 *
 *  主题名 Topic Name
 *  主题名（Topic Name）用于识别有效载荷数据应该被发布到哪一个信息通道。
 *  主题名必须是PUBLISH报文可变报头的第一个字段。它必须是 1.5.3节定义的UTF-8编码的字符串 [MQTT-3.3.2-1]。
 *  PUBLISH报文中的主题名不能包含通配符 [MQTT-3.3.2-2]。
 *  服务端发送给订阅客户端的PUBLISH报文的主题名必须匹配该订阅的主题过滤器[MQTT-3.3.2-3]。
 *
 *  报文标识符 Packet Identifier
 *  只有当QoS等级是1或2时，报文标识符（Packet Identifier）字段才能出现在PUBLISH报文中。2.3.1节提供了有关报文标识符的更多信息。
 *
 *  示例：
 *  主题名			a/b
 *  报文标识符		10
 *
 *  对应的字节格式
 *  byte1~2			MSB 0000 0000			LSB	0000 0011
 * 	byte3			‘a’ (0x61)				0110 0001
 * 	byte4			‘/’ (0x2F)				0010 1111
 * 	byte5			‘b’ (0x62)				0110 0010
 * 	byte6~7			报文标识符 MSB (0)		报文标识符 LSB (10)	0000 1010
 *
 * <p>
 *  有效载荷 Payload
 *	有效载荷包含将被发布的应用消息。数据的内容和格式是应用特定的。
 *	有效载荷的长度这样计算：用固定报头中的剩余长度字段的值减去可变报头的长度。
 *	包含零长度有效载荷的PUBLISH报文是合法的。
 */
@Singleton
public class MqttPublishDecoder extends AbstractMqttDecoder<Publish> {
	@Inject
	public MqttPublishDecoder(final @NotNull MqttServerDisconnector disconnector) {
		super(disconnector);
	}

	@Override
	public Publish decode(@NotNull Channel channel, @NotNull ByteBuf buf, byte header) {
		final int qos = decodeQoS(channel, header);
		if (qos == DISCONNECTED) {
			return null;
		}

		final Boolean dup = decodeDup(channel, header, qos);
		if (dup == null) {
			return null;
		}

		final Boolean retain = decodeRetain(channel, header);
		if (retain == null) {
			return null;
		}

		final int utf8StringLength = decodeUTF8StringLength(channel, buf, "topic", MessageType.PUBLISH);
		if (utf8StringLength == DISCONNECTED) {
			return null;
		}

		final String topicName;
		topicName = decodeUTF8Topic(channel, buf, utf8StringLength, true, "topic", MessageType.PUBLISH);
		if (topicName == null) {
			return null;
		}

		if (topicInvalid(channel, topicName, MessageType.PUBLISH)) {
			return null;
		}

		final int packetId;
		if (qos > 0) {
			packetId = decodePacketIdentifier(channel, buf);
			if (packetId == 0) {
				return null;
			}
		} else {
			packetId = 0;
		}

		final byte[] payload = new byte[buf.readableBytes()];
		buf.readBytes(payload);

		return new Publish(LmsServerId.instance.get(), topicName, payload, QoS.getByCode(qos), retain, packetId, dup, -1, -1);
	}

	private int decodeQoS(final @NotNull Channel channel, final byte header) {
		// qos 值只能是0~2
		final int qos = (header & 0b0000_0110) >> 1;

		if (qos == 3) {
			disconnector.disconnect(channel,
					"client (IP: {}) 发送的PUBLISH消息QoS不合法",
					"发送的PUBLISH消息QoS不合法",
					MqttDisconnectReasonCode.MALFORMED_PACKET,
					"发送的PUBLISH消息QoS不合法", false);
			return DISCONNECTED;
		}
		return qos;
	}

	@Nullable
	private Boolean decodeDup(final @NotNull Channel channel, final byte header, final int qos) {
		final boolean dup = Bytes.isBitSet(header, 3);

		// 对于QoS 0的消息，DUP标志必须设置为0
		if (qos == 0 && dup) {
			disconnector.disconnect(channel,
					"(IP: {}) 发送的PUBLISH消息QoS值为0并且 DUP 位设置为1",
					"发送的PUBLISH消息QoS值为0并且 DUP 位设置为1",
					MqttDisconnectReasonCode.PROTOCOL_ERROR,
					"发送的PUBLISH消息QoS值为0并且 DUP 位设置为1", false);
			return null;
		}
		return dup;
	}

	@Nullable
	private Boolean decodeRetain(final @NotNull Channel channel, final byte header) {
		final boolean retained = Bytes.isBitSet(header, 0);

		return retained;
	}

	protected int decodePacketIdentifier(final @NotNull Channel channel, final @NotNull ByteBuf buf) {
		final int packetIdentifier = buf.readUnsignedShort();
		if (packetIdentifier == 0) {
			disconnector.disconnect(channel,
					"client (IP: {}) 发送的PUBLISH消息QoS > 0 但是packet id是0",
					"发送的PUBLISH消息QoS > 0 但是packet id是0",
					MqttDisconnectReasonCode.PROTOCOL_ERROR,
					"发送的PUBLISH消息QoS > 0 但是packet id是0", false);
		}
		return packetIdentifier;
	}
}
