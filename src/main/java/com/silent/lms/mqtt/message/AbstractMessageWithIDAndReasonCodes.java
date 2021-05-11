package com.silent.lms.mqtt.message;

import com.google.common.collect.ImmutableList;
import com.silent.lms.annotations.NotNull;
import com.silent.lms.mqtt.reason.MqttReasonCode;
import lombok.Getter;

/**
 *
 * @author gy
 * @date 2021/5/11.
 * @version 1.0
 * @description:
 */
public abstract class AbstractMessageWithIDAndReasonCodes<R extends MqttReasonCode> extends AbstractMessageWithID {
	private final ImmutableList<R> reasonCodes;

	protected AbstractMessageWithIDAndReasonCodes(final int packetId, @NotNull final ImmutableList<R> reasonCodes) {
		super.packetId = packetId;
		this.reasonCodes = reasonCodes;
	}

	@NotNull
	public ImmutableList<R> getReasonCodes() {
		return reasonCodes;
	}
}
