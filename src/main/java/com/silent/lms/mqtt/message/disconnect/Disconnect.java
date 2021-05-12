package com.silent.lms.mqtt.message.disconnect;

import com.google.common.collect.ImmutableList;
import com.silent.lms.annotations.NotNull;
import com.silent.lms.mqtt.message.AbstractMessageWithIDAndReasonCodes;
import com.silent.lms.mqtt.message.MessageType;
import com.silent.lms.mqtt.reason.MqttDisconnectReasonCode;

/**
 *
 * @author gy
 * @date 2021/5/11.
 * @version 1.0
 * @description:
 */
public class Disconnect extends AbstractMessageWithIDAndReasonCodes<MqttDisconnectReasonCode> implements Mqtt3Disconnect {
	public Disconnect() {
		super(0, ImmutableList.of(MqttDisconnectReasonCode.NORMAL_DISCONNECTION));
	}

	@Override
	public MessageType getType() {
		return MessageType.DISCONNECT;
	}
}
