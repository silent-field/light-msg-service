package com.silent.lms.mqtt.handler.disconnect;

import com.google.common.base.Preconditions;
import com.silent.lms.annotations.NotNull;
import com.silent.lms.annotations.Nullable;
import com.silent.lms.logging.EventLog;
import com.silent.lms.mqtt.event.OnAuthFailedEvent;
import com.silent.lms.mqtt.event.OnServerDisconnectEvent;
import com.silent.lms.mqtt.reason.MqttDisconnectReasonCode;
import com.silent.lms.util.ChannelUtils;
import io.netty.channel.Channel;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author gy
 * @version 1.0
 * @date 2021/5/8.
 * @description:
 */
@Log4j2
@Singleton
public class MqttServerDisconnectorImpl implements MqttServerDisconnector {
	private final @NotNull EventLog eventLog;

	@Inject
	public MqttServerDisconnectorImpl(final @NotNull EventLog eventLog) {
		this.eventLog = eventLog;
	}

	@Override
	public void disconnect(@NotNull Channel channel, @Nullable String logMessage,
						   @Nullable String eventLogMessage, final @Nullable MqttDisconnectReasonCode reasonCode,
						   @Nullable String reasonString, boolean isAuthentication) {
		Preconditions.checkNotNull(channel, "Channel不能为空");

		if (channel.isActive()) {
			log(channel, logMessage, eventLogMessage);
			fireEvents(channel, reasonCode, reasonString, isAuthentication);
			closeConnection(channel);
		}
	}

	private void log(
			final @NotNull Channel channel,
			final @Nullable String logMessage,
			final @Nullable String eventLogMessage) {

		if (log.isDebugEnabled() && StringUtils.isNotBlank(logMessage)) {
			log.debug(logMessage, ChannelUtils.getChannelIP(channel).or("UNKNOWN"));
		}

		if (StringUtils.isNotBlank(eventLogMessage)) {
			eventLog.clientWasDisconnected(channel, eventLogMessage);
		}
	}

	private void fireEvents(final @NotNull Channel channel,
							final @Nullable MqttDisconnectReasonCode reasonCode,
							final @Nullable String reasonString,
							final boolean isAuthentication) {
		channel.pipeline().fireUserEventTriggered(isAuthentication ?
				new OnAuthFailedEvent(reasonCode, reasonString) :
				new OnServerDisconnectEvent(reasonCode, reasonString));
	}

	private void closeConnection(
			final @NotNull Channel channel) {
		channel.close();
	}
}
