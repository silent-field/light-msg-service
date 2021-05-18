package com.silent.lms.mqtt.reason;

public enum MqttCommonReasonCode implements MqttReasonCode {
	SUCCESS(0x00),
	UNSPECIFIED_ERROR(0x80),
	MALFORMED_PACKET(0x81),
	PROTOCOL_ERROR(0x82),
	TOPIC_NAME_INVALID(0x90),
	PACKET_TOO_LARGE(0x95),
	;

	private final int code;

	MqttCommonReasonCode(final int code) {
		this.code = code;
	}

	@Override
	public int getCode() {
		return code;
	}
}