package com.silent.lms.mqtt.reason;

import com.silent.lms.annotations.NotNull;
import com.silent.lms.annotations.Nullable;

/**
 *
 * @author gy
 * @date 2021/5/8.
 * @version 1.0
 * @description:
 */
public enum MqttSubAckReasonCode implements MqttReasonCode {
	GRANTED_QOS_0(0x00),
	GRANTED_QOS_1(0x01),
	GRANTED_QOS_2(0x02),
	UNSPECIFIED_ERROR(MqttCommonReasonCode.UNSPECIFIED_ERROR);

	public static @Nullable MqttSubAckReasonCode getByCode(final int code) {
		for (final MqttSubAckReasonCode reasonCode : values()) {
			if (reasonCode.code == code) {
				return reasonCode;
			}
		}
		return null;
	}

	private final int code;

	MqttSubAckReasonCode(final int code) {
		this.code = code;
	}

	@Override
	public int getCode() {
		return code;
	}

	MqttSubAckReasonCode(final @NotNull MqttCommonReasonCode reasonCode) {
		this(reasonCode.getCode());
	}
}
