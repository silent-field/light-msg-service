package com.silent.lms.mqtt.event;

import com.silent.lms.annotations.Immutable;
import com.silent.lms.annotations.Nullable;
import com.silent.lms.mqtt.reason.MqttDisconnectReasonCode;

@Immutable
public class OnAuthFailedEvent extends EventWithReasonCode{
	public OnAuthFailedEvent(
			final @Nullable MqttDisconnectReasonCode reasonCode,
			final @Nullable String reasonString) {
		super(reasonCode, reasonString);
	}

}