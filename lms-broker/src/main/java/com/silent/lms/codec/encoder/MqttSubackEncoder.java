package com.silent.lms.codec.encoder;

import com.silent.lms.annotations.NotNull;
import com.silent.lms.codec.encoder.helper.AbstractVariableHeaderLengthEncoder;
import com.silent.lms.mqtt.handler.disconnect.MqttServerDisconnector;
import com.silent.lms.mqtt.message.suback.Suback;
import com.silent.lms.mqtt.reason.MqttSubAckReasonCode;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author gy
 * @date 2021/5/8.
 * @version 1.0
 * @description:
 *
 * 服务端发送SUBACK报文给客户端，用于确认它已收到并且正在处理SUBSCRIBE报文。
 * SUBACK报文包含一个返回码清单，它们指定了SUBSCRIBE请求的每个订阅被授予的最大QoS等级。
 * <p>
 *  固定报头
 *  byte1		MQTT控制报文类型 (9)		1001 0000
 *  byte2		剩余长度 				等于可变报头的长度加上有效载荷的长度。
 * <p>
 * 	可变报头
 * 	byte3 报文标识符 MSB
 * 	byte4 报文标识符 LSB
 * <p>
 *  有效载荷
 *  有效载荷包含一个返回码清单。每个返回码对应等待确认的SUBSCRIBE报文中的一个主题过滤器。
 *  返回码的顺序必须和SUBSCRIBE报文中主题过滤器的顺序相同 [MQTT-3.9.3-1]
 *
 *  允许的返回码值：
 * 	0x00 - 最大QoS 0						0000 0000
 * 	0x01 - 成功 – 最大QoS 1				0000 0001
 * 	0x02 - 成功 – 最大 QoS 2				0000 0010
 * 	0x80 - Failure  失败					1000 0000
 * 	0x00, 0x01, 0x02, 0x80之外的SUBACK返回码是保留的，不能使用[MQTT-3.9.3-2]。
 *
 *
 */
public class MqttSubackEncoder extends AbstractVariableHeaderLengthEncoder<Suback> implements MqttEncoder<Suback> {
	private static final byte SUBACK_FIXED_HEADER = (byte) 0b1001_0000;
	public static final int VARIABLE_HEADER_SIZE = 2;

	private final @NotNull MqttServerDisconnector mqttServerDisconnector;

	public MqttSubackEncoder(final @NotNull MqttServerDisconnector mqttServerDisconnector) {
		this.mqttServerDisconnector = mqttServerDisconnector;
	}

	@Override
	public void encode(@NotNull ChannelHandlerContext ctx, @NotNull Suback msg, @NotNull ByteBuf out) {
		// 固定报头 - MQTT控制报文类型 (9)
		out.writeByte(SUBACK_FIXED_HEADER);

		// 固定报头 - 剩余长度
		final int remainingLength = remainingLength(msg);
		createRemainingLength(remainingLength, out);

		// 可变报头 - 报文标识 两个字节
		out.writeShort(msg.getPacketId());

		// 有效载荷
		for (final MqttSubAckReasonCode granted : msg.getReasonCodes()) {
			out.writeByte(granted.getCode());
		}
	}

	@Override
	protected int remainingLength(Suback msg) {
		return msg.getReasonCodes().size() + VARIABLE_HEADER_SIZE;
	}
}
