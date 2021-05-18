package com.silent.lms.mqtt.message.pingreq;

import com.silent.lms.mqtt.message.AbstractMessage;
import com.silent.lms.mqtt.message.MessageType;

/**
 *
 * @author gy
 * @date 2021/5/11.
 * @version 1.0
 * @description:
 */
public class PingReq extends AbstractMessage {
	public static final PingReq INSTANCE = InstanceHolder.instance;

	@Override
	public int getEncodedLength() {
		return 2;
	}

	@Override
	public MessageType getType() {
		return MessageType.PINGREQ;
	}

	// ------------------- 单例
	private static class InstanceHolder {
		private static final PingReq instance = new PingReq();
	}
}
