package com.silent.lms.mqtt.message.unsuback;

import com.google.common.collect.ImmutableList;
import com.silent.lms.annotations.NotNull;
import com.silent.lms.mqtt.message.AbstractMessageWithIDAndReasonCodes;
import com.silent.lms.mqtt.message.MessageType;
import com.silent.lms.mqtt.reason.MqttUnsubAckReasonCode;

import java.util.Collection;

/**
 *
 * @author gy
 * @date 2021/5/11.
 * @version 1.0
 * @description:
 */
public class Unsuback extends AbstractMessageWithIDAndReasonCodes implements Mqtt3Unsuback {
	public Unsuback(final int packetId, final @NotNull Collection<MqttUnsubAckReasonCode> entries) {
		super(packetId, ImmutableList.copyOf(entries));
	}

	@Override
	public MessageType getType() {
		return MessageType.UNSUBACK;
	}
}
