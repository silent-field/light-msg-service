package com.silent.lms.mqtt.reason;

import com.silent.lms.annotations.NotNull;

/**
 *
 * @author gy
 * @date 2021/5/11.
 * @version 1.0
 * @description:
 */
public enum MqttUnsubAckReasonCode implements MqttReasonCode{
	SUCCESS(MqttCommonReasonCode.SUCCESS),
	;

	private final int code;

	MqttUnsubAckReasonCode(final int code) {
		this.code = code;
	}

	MqttUnsubAckReasonCode(final @NotNull MqttCommonReasonCode reasonCode) {
		this(reasonCode.getCode());
	}

	@Override
	public int getCode() {
		return code;
	}
}
