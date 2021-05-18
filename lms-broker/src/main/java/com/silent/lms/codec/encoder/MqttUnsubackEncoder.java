package com.silent.lms.codec.encoder;

import com.silent.lms.annotations.NotNull;
import com.silent.lms.codec.encoder.helper.FixedSizeMessageEncoder;
import com.silent.lms.mqtt.message.unsuback.Unsuback;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

/**
 *
 * @author gy
 * @date 2021/5/11.
 * @version 1.0
 * @description:
 *
 * 服务端发送 UNSUBACK 报文给客户端，用于确认它已收到并且正在处理UNSUBSCRIBE报文。
 * <p>
 *  固定报头
 *  byte1		MQTT控制报文类型 (11)		1011 0000
 *  byte2		剩余长度(2) 				0000 0010
 * <p>
 * 	可变报头 包含等待确认的UNSUBSCRIBE报文的报文标识符。
 * 	byte3 报文标识符 MSB
 * 	byte4 报文标识符 LSB
 * <p>
 *  有效载荷
 *  UNSUBACK报文没有有效载荷。
 */
public class MqttUnsubackEncoder extends FixedSizeMessageEncoder<Unsuback>  implements MqttEncoder<Unsuback>{
	private static final byte UNSUBACK_FIXED_HEADER = (byte) 0b1011_0000;
	private static final byte UNSUBACK_REMAINING_LENGTH = 0b0000_0010;
	public static final int ENCODED_UNSUBACK_SIZE = 4;

	@Override
	public void encode(@NotNull ChannelHandlerContext ctx, @NotNull Unsuback msg, @NotNull ByteBuf out) {
		out.writeByte(UNSUBACK_FIXED_HEADER);
		out.writeByte(UNSUBACK_REMAINING_LENGTH);

		out.writeShort(msg.getPacketId());
	}

	@Override
	public int bufferSize(@NotNull ChannelHandlerContext ctx, @NotNull Unsuback msg) {
		return ENCODED_UNSUBACK_SIZE;
	}
}
