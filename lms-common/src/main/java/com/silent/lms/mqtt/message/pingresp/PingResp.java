package com.silent.lms.mqtt.message.pingresp;

import com.silent.lms.mqtt.message.AbstractMessage;
import com.silent.lms.mqtt.message.MessageType;

/**
 *
 * @author gy
 * @date 2021/5/11.
 * @version 1.0
 * @description:
 */
public class PingResp extends AbstractMessage {
	public static final PingResp INSTANCE = InstanceHolder.instance;

	@Override
	public int getEncodedLength() {
		return 2;
	}

	@Override
	public MessageType getType() {
		return MessageType.PINGRESP;
	}

	// ------------------- 单例
	private static class InstanceHolder {
		private static final PingResp instance = new PingResp();
	}


}
