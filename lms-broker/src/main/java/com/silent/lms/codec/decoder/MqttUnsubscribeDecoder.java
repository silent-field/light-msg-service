package com.silent.lms.codec.decoder;

import com.google.inject.Inject;
import com.silent.lms.annotations.NotNull;
import com.silent.lms.mqtt.handler.disconnect.MqttServerDisconnector;
import com.silent.lms.mqtt.message.MessageType;
import com.silent.lms.mqtt.message.unsubscribe.Unsubscribe;
import com.silent.lms.mqtt.reason.MqttDisconnectReasonCode;
import com.silent.lms.util.Strings;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gy
 * @date 2021/5/11.
 * @version 1.0
 * @description:
 * <p>
 *  固定报头 Fixed header
 *  byte 1      1010 0010(报文类型：UNSUBSCRIBE，标志位固定0010，如果不是必须断开连接)
 *  byte 2...   剩余长度等于可变报头的长度加上有效载荷的长度。
 * <p>
 *  可变报头 Variable header
 *  可变报头包含报文标识符，两个字节
 * <p>
 *  有效载荷 Payload
 *	1.UNSUBSCRIBE报文的有效载荷包含客户端想要取消订阅的主题过滤器列表。
 *	2.UNSUBSCRIBE报文中的主题过滤器必须是连续打包的、按照1.5.3节定义的UTF-8编码字符串 [MQTT-3.10.3-1]。
 *	3.UNSUBSCRIBE报文的有效载荷必须至少包含一个消息过滤器。没有有效载荷的UNSUBSCRIBE报文是违反协议的 [MQTT-3.10.3-2]。
 *
 * <p>
 * 	示例：
 * 	主题过滤器			“a/b”
 * 	主题过滤器			“c/d”
 *
 * 	对应的字节格式
 * 	byte1~2			MSB 0000 0000		LSB	0000 0011
 * 	byte3			‘a’ (0x61)				0110 0001
 * 	byte4			‘/’ (0x2F)				0010 1111
 * 	byte5			‘b’ (0x62)				0110 0010
 *
 * 	byte6~8			MSB 0000 0000		LSB	0000 0011
 * 	byte8			‘c’ (0x63)				0110 0011
 * 	byte9			‘/’ (0x2F)				0010 1111
 * 	byte10			‘d’ (0x64)				0110 0100
 */
@Singleton
public class MqttUnsubscribeDecoder extends AbstractMqttDecoder<Unsubscribe> {
	@Inject
	public MqttUnsubscribeDecoder(final @NotNull MqttServerDisconnector disconnector) {
		super(disconnector);
	}

	@Override
	public Unsubscribe decode(@NotNull Channel channel, @NotNull ByteBuf buf, byte header) {
		if ((header & 0b0000_1111) != 2) {
			disconnectByInvalidFixedHeader(channel, MessageType.UNSUBSCRIBE);
			buf.clear();
			return null;
		}

		if (buf.readableBytes() < 2) {
			disconnectByNoMessageId(channel, MessageType.UNSUBSCRIBE);
			buf.clear();
			return null;
		}

		final int messageId = buf.readUnsignedShort();
		final List<String> topics = new ArrayList<>();

		while (buf.isReadable()) {
			final String topic = Strings.getPrefixedString(buf);
			if (isInvalidTopic(channel, topic)) {
				disconnector.disconnect(channel,
						"client (IP: {}) 发送的UNSUBSCRIBE的有效负载没有 topic",
						"发送的UNSUBSCRIBE的有效负载没有 topic",
						MqttDisconnectReasonCode.MALFORMED_PACKET,
						"发送的UNSUBSCRIBE的有效负载没有 topic", false);
				buf.clear();
				return null;
			}
			topics.add(topic);
		}

		if (topics.isEmpty()) {
			disconnector.disconnect(channel,
					"client (IP: {}) 发送的UNSUBSCRIBE的有效负载没有 topic 过滤器",
					"发送的UNSUBSCRIBE的有效负载没有 topic 过滤器",
					MqttDisconnectReasonCode.PROTOCOL_ERROR,
					"发送的UNSUBSCRIBE的有效负载没有 topic 过滤器", false);
			buf.clear();
			return null;
		}
		return new Unsubscribe(topics, messageId);
	}
}
