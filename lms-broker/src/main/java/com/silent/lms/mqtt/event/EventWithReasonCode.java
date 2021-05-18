package com.silent.lms.mqtt.event;

import com.silent.lms.annotations.Nullable;
import com.silent.lms.mqtt.reason.MqttDisconnectReasonCode;

/**
 * @author gy
 * @version 1.0
 * @date 2021/5/8.
 * @description:
 */
public abstract class EventWithReasonCode {
	private final @Nullable MqttDisconnectReasonCode reasonCode;
	private final @Nullable String reasonString;

	public EventWithReasonCode(
			final @Nullable MqttDisconnectReasonCode reasonCode,
			final @Nullable String reasonString) {

		this.reasonCode = reasonCode;
		this.reasonString = reasonString;
	}

	public @Nullable MqttDisconnectReasonCode getReasonCode() {
		return reasonCode;
	}

	public @Nullable
	String getReasonString() {
		return reasonString;
	}
}
