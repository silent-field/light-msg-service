package com.silent.lms.codec.decoder;

import com.google.inject.Inject;
import com.silent.lms.annotations.NotNull;
import com.silent.lms.mqtt.handler.disconnect.MqttServerDisconnector;
import com.silent.lms.mqtt.message.pingreq.PingReq;
import com.silent.lms.mqtt.reason.MqttDisconnectReasonCode;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

/**
 *
 * @author gy
 * @date 2021/5/11.
 * @version 1.0
 * @description:
 *
 * 客户端发送PINGREQ报文给服务端的。用于：
 * 在没有任何其它控制报文从客户端发给服务的时，告知服务端客户端还活着。
 * 请求服务端发送 响应确认它还活着。
 * 使用网络以确认网络连接没有断开。
 *
 * <p>
 *  固定报头 Fixed header
 *  byte 1      1100 0000(报文类型(12)：PINGREQ，标志位固定0000，如果不是必须断开连接)
 *  byte 2		0000 0000
 * <p>
 *  可变报头 Variable header
 *  PINGREQ报文没有可变报头。
 * <p>
 *  有效载荷 Payload
 *	PINGREQ报文没有有效载荷。
 * <P>
 *  服务端必须发送 PINGRESP报文响应客户端的PINGREQ报文 [MQTT-3.12.4-1]。
 */
public class MqttPingReqDecoder extends MqttDecoder<PingReq> {
	@NotNull
	private final MqttServerDisconnector serverDisconnector;

	@Inject
	public MqttPingReqDecoder(final @NotNull MqttServerDisconnector serverDisconnector) {
		this.serverDisconnector = serverDisconnector;
	}

	@Override
	public PingReq decode(@NotNull Channel channel, @NotNull ByteBuf buf, byte header) {
		if (!validateHeader(header)) {
			serverDisconnector.disconnect(channel,
					"client (IP: {}) 发送的PINGREQ fixed header不合法",
					"发送的PINGREQ fixed header不合法",
					MqttDisconnectReasonCode.MALFORMED_PACKET,
					"发送的PINGREQ fixed header不合法", false);
			buf.clear();
			return null;
		}

		return PingReq.INSTANCE;
	}
}
