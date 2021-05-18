package com.silent.lms.codec.decoder;

import com.google.inject.Inject;
import com.silent.lms.annotations.NotNull;
import com.silent.lms.mqtt.handler.disconnect.MqttServerDisconnector;
import com.silent.lms.mqtt.message.MessageType;
import com.silent.lms.mqtt.message.disconnect.Disconnect;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

import javax.inject.Singleton;

/**
 *
 * @author gy
 * @date 2021/5/11.
 * @version 1.0
 * @description:
 *
 * DISCONNECT报文是客户端发给服务端的最后一个控制报文。表示客户端正常断开连接。
 *
 * <p>
 *  固定报头 Fixed header
 *  byte 1      1110 0000(报文类型(14)：DISCONNECT，标志位固定0000，如果不是必须断开连接)
 *  byte 2		0000 0000
 * <p>
 *  可变报头 Variable header
 *   DISCONNECT报文没有可变报头。
 * <p>
 *  有效载荷 Payload
 *	 DISCONNECT报文没有有效载荷。
 */
@Singleton
public class MqttDisconnectDecoder extends AbstractMqttDecoder<Disconnect>{
	private static final Disconnect DISCONNECT = new Disconnect();

	@Inject
	public MqttDisconnectDecoder(final @NotNull MqttServerDisconnector disconnector) {
		super(disconnector);
	}

	@Override
	public Disconnect decode(@NotNull Channel channel, @NotNull ByteBuf buf, byte header) {
		if (!validateHeader(header)) {
			disconnectByInvalidFixedHeader(channel, MessageType.DISCONNECT);
			return null;
		}
		return DISCONNECT;
	}
}
