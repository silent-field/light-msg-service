package com.silent.lms.mqtt.reason;

import com.silent.lms.annotations.NotNull;

/**
 *
 * @author gy
 * @date 2021/5/14.
 * @version 1.0
 * @description:
 */
public enum MqttConnAckReasonCode implements MqttReasonCode {
	ACCEPTED(MqttCommonReasonCode.SUCCESS),
	REFUSED_UNACCEPTABLE_PROTOCOL_VERSION(0x01),
	REFUSED_NOT_AUTHORIZED(0x02),
	TOPIC_NAME_INVALID(MqttCommonReasonCode.TOPIC_NAME_INVALID),
	;

	private final int code;

	MqttConnAckReasonCode(final int code) {
		this.code = code;
	}

	MqttConnAckReasonCode(final @NotNull MqttCommonReasonCode reasonCode) {
		this(reasonCode.getCode());
	}

	@Override
	public int getCode() {
		return code;
	}
}
