package com.silent.lms.mqtt.reason;

import com.silent.lms.annotations.NotNull;

/**
 * @author gy
 * @version 1.0
 * @date 2021/5/8.
 * @description:
 */
public enum MqttDisconnectReasonCode implements MqttReasonCode {
	PROTOCOL_ERROR(MqttCommonReasonCode.PROTOCOL_ERROR),
	MALFORMED_PACKET(MqttCommonReasonCode.MALFORMED_PACKET),
	PACKET_TOO_LARGE(MqttCommonReasonCode.PACKET_TOO_LARGE),
	;

	private final int code;

	MqttDisconnectReasonCode(final int code) {
		this.code = code;
	}

	MqttDisconnectReasonCode(final @NotNull MqttCommonReasonCode reasonCode) {
		this(reasonCode.getCode());
	}

	@Override
	public int getCode() {
		return code;
	}
}
