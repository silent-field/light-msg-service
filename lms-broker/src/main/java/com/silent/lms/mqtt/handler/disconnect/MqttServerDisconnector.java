package com.silent.lms.mqtt.handler.disconnect;

import com.silent.lms.annotations.NotNull;
import com.silent.lms.annotations.Nullable;
import com.silent.lms.mqtt.reason.MqttDisconnectReasonCode;
import io.netty.channel.Channel;

/**
 * @author gy
 * @version 1.0
 * @date 2021/5/8.
 * @description:
 */
public interface MqttServerDisconnector {
	default void logAndClose(
			final @NotNull Channel channel,
			final @Nullable String logMessage,
			final @Nullable String eventLogMessage) {
		disconnect(channel, logMessage, eventLogMessage, null, null, false);
	}

	/**
	 * @param channel
	 * @param logMessage       用于普通log打印
	 * @param eventLogMessage  用于eventLog打印
	 * @param reasonString     用于传递netty 事件
	 * @param isAuthentication 鉴权不通过
	 */
	void disconnect(
			final @NotNull Channel channel,
			final @Nullable String logMessage,
			final @Nullable String eventLogMessage,
			final @Nullable MqttDisconnectReasonCode reasonCode,
			final @Nullable String reasonString,
			final boolean isAuthentication);
}
