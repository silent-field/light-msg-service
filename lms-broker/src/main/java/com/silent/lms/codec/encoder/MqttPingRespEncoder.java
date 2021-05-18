package com.silent.lms.codec.encoder;

import com.silent.lms.annotations.NotNull;
import com.silent.lms.codec.encoder.helper.FixedSizeMessageEncoder;
import com.silent.lms.mqtt.message.pingresp.PingResp;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

/**
 *
 * @author gy
 * @date 2021/5/11.
 * @version 1.0
 * @description:
 *
 * 服务端发送PINGRESP报文响应客户端的PINGREQ报文。表示服务端还活着。
 * 保持连接（Keep Alive）处理中用到这个报文
 *
 * <p>
 *  固定报头 Fixed header
 *  byte 1      1101 0000(报文类型(13)：PINGRESP，标志位固定0000，如果不是必须断开连接)
 *  byte 2		0000 0000
 * <p>
 *  可变报头 Variable header
 *  PINGREQ报文没有可变报头。
 * <p>
 *  有效载荷 Payload
 *	PINGREQ报文没有有效载荷。
 */
public class MqttPingRespEncoder extends FixedSizeMessageEncoder<PingResp>  implements MqttEncoder<PingResp>{
	private static final byte PINGRESP_FIXED_HEADER = (byte) 0b1101_0000;
	private static final byte PINGRESP_REMAINING_LENGTH = (byte) 0b0000_0000;
	public static final int ENCODED_PINGRESP_SIZE = 2;

	@Override
	public void encode(@NotNull ChannelHandlerContext ctx, @NotNull PingResp msg, @NotNull ByteBuf out) {
		out.writeByte(PINGRESP_FIXED_HEADER);
		out.writeByte(PINGRESP_REMAINING_LENGTH);
	}

	@Override
	public int bufferSize(@NotNull ChannelHandlerContext ctx, @NotNull PingResp msg) {
		return ENCODED_PINGRESP_SIZE;
	}
}
