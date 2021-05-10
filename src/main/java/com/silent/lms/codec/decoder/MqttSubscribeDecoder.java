package com.silent.lms.codec.decoder;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.silent.lms.annotations.NotNull;
import com.silent.lms.mqtt.handler.disconnect.MqttServerDisconnector;
import com.silent.lms.mqtt.message.MessageType;
import com.silent.lms.mqtt.message.QoS;
import com.silent.lms.mqtt.message.subscribe.Subscribe;
import com.silent.lms.mqtt.message.subscribe.Topic;
import com.silent.lms.mqtt.reason.MqttDisconnectReasonCode;
import com.silent.lms.util.Strings;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import lombok.extern.log4j.Log4j2;

import javax.inject.Singleton;

/**
 * @author gy
 * @version 1.0
 * @date 2021/5/8.
 * @description:
 * <p>
 *  固定报头 Fixed header
 *  byte 1      1000 0010(报文类型：SUBSCRIBE，标志位固定0010，如果不是必须断开连接)
 *  byte 2...   剩余长度等于可变报头的长度（10字节）加上有效载荷的长度
 * <p>
 *  可变报头 Variable header
 *  可变报头包含报文标识符，两个字节
 * <p>
 *  有效载荷 Payload
 *	1.SUBSCRIBE报文的有效载荷包含了一个主题过滤器列表，它们表示客户端想要订阅的主题。
 *	2.服务端应该支持包含通配符的主题过滤器，如果服务端选择不支持包含通配符的主题过滤器，必须拒绝任何包含通配符过滤器的订阅请求
 *	3.每一个过滤器后面跟着一个字节，这个字节被叫做 服务质量要求（Requested QoS）。它给出了服务端向客户端发送应用消息所允许的最大QoS等级。
 *	4.SUBSCRIBE报文的有效载荷必须包含至少一对主题过滤器 和 QoS等级字段组合。没有有效载荷的SUBSCRIBE报文是违反协议的
 *	5.当前版本的协议没有用到服务质量要求（Requested QoS）字节的高六位。如果有效载荷中的任何位是非零值，或者QoS不等于0,1或2，
 *		服务端必须认为SUBSCRIBE报文是不合法的并关闭网络连接
 *
 * 	格式
 * 	两个字节长度						byte1 长度 MSB	byte2 长度 LSB
 * 	主题过滤器（Topic Filter） 		bytes 3..N
 * 	服务质量要求（Requested QoS）		byte N+1(每个字节的低2位，QoS值范围0~2)
 *
 * <p>
 * 	示例：
 * 	主题名			“a/b”
 * 	服务质量要求		0x01
 * 	主题名			“c/d”
 * 	服务质量要求		0x02
 *
 * 	对应的字节格式
 * 	byte1~2			MSB 0000 0000		LSB	0000 0011
 * 	byte3			‘a’ (0x61)				0110 0001
 * 	byte4			‘/’ (0x2F)				0010 1111
 * 	byte5			‘b’ (0x62)				0110 0010
 * 	byte6			QoS(1)					0000 0001
 *
 * 	byte7~8			MSB 0000 0000		LSB	0000 0011
 * 	byte9			‘c’ (0x63)				0110 0011
 * 	byte10			‘/’ (0x2F)				0010 1111
 * 	byte11			‘d’ (0x64)				0110 0100
 * 	byte12			QoS(2)					0000 0010
 *
 */
@Log4j2
@Singleton
public class MqttSubscribeDecoder extends AbstractMqttDecoder<Subscribe> {
	@Inject
	public MqttSubscribeDecoder(final @NotNull MqttServerDisconnector disconnector) {
		super(disconnector);
	}

	@Override
	public Subscribe decode(@NotNull Channel channel, @NotNull ByteBuf buf, byte header) {
		// 低4位必须是0010
		if ((header & 0b0000_1111) != 2) {
			disconnectByInvalidFixedHeader(channel, MessageType.SUBSCRIBE);
			buf.clear();
			return null;
		}

		// 报文ID，固定两位
		final int messageId;
		if (buf.readableBytes() >= 2) {
			messageId = buf.readUnsignedShort();
		} else {
			disconnectByNoMessageId(channel, MessageType.SUBSCRIBE);
			buf.clear();
			return null;
		}

		final ImmutableList.Builder<Topic> topics = new ImmutableList.Builder<>();
		if (!buf.isReadable()) {
			disconnector.disconnect(channel,
					"client (IP: {})发送了一个没有订阅topic的SUBSCRIBE消息",
					"发送了一个没有订阅topic的SUBSCRIBE消息",
					MqttDisconnectReasonCode.PROTOCOL_ERROR,
					"没有订阅topic的SUBSCRIBE消息", false);
			buf.clear();
			return null;
		}

		while (buf.isReadable()) {
			final String topic = Strings.getPrefixedString(buf);

			if (isInvalidTopic(channel, topic)) {
				disconnector.disconnect(
						channel,
						null, //already logged
						"SUBSCRIBE包含非法的topic过滤器",
						MqttDisconnectReasonCode.MALFORMED_PACKET,
						"存在非法的topic过滤器",false);
				return null;
			}

			if (buf.readableBytes() == 0) {
				disconnector.disconnect(channel,
						"client (IP: {}) 发送的SUBSCRIBE报文存在没有QoS的主题过滤器",
						"发送的SUBSCRIBE报文存在没有QoS的主题过滤器",
						MqttDisconnectReasonCode.PROTOCOL_ERROR,
						"发送的SUBSCRIBE报文存在没有QoS的主题过滤器",false);
				buf.clear();
				return null;
			}

			final int qos = buf.readByte();

			if(!QoS.isValidValue(qos)){
				disconnector.disconnect(channel,
						"client (IP: {}) 发送的SUBSCRIBE报文存在QoS的值大于2",
						"发送的SUBSCRIBE报文存在QoS的值大于2",
						MqttDisconnectReasonCode.PROTOCOL_ERROR,
						"发送的SUBSCRIBE报文存在QoS的值大于2",false);
				buf.clear();
				return null;
			}

			topics.add(new Topic(topic, QoS.getByCode(qos)));
		}

		return new Subscribe(topics.build(), messageId);
	}
}
