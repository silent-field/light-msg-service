package com.silent.lms.mqtt.message.suback;

import com.google.common.collect.ImmutableList;
import com.silent.lms.mqtt.message.AbstractMessageWithID;
import com.silent.lms.mqtt.message.MessageType;
import com.silent.lms.mqtt.message.subscribe.Mqtt3Subscribe;
import com.silent.lms.mqtt.reason.MqttSubAckReasonCode;

import java.util.List;

/**
 *
 * @author gy
 * @date 2021/5/8.
 * @version 1.0
 * @description:
 */
public class Suback extends AbstractMessageWithID implements Mqtt3SUBACK {
	private ImmutableList<MqttSubAckReasonCode> reasonCodes;
	public Suback(final int packetId, List<MqttSubAckReasonCode> reasonCodes){
		super(packetId);
		this.reasonCodes = ImmutableList.copyOf(reasonCodes);
	}

	@Override
	public MessageType getType() {
		return MessageType.SUBACK;
	}

	@Override
	public ImmutableList<MqttSubAckReasonCode> getReasonCodes() {
		return reasonCodes;
	}
}
