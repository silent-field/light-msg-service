package com.silent.lms.logging;

import com.silent.lms.annotations.NotNull;
import com.silent.lms.annotations.Nullable;
import com.silent.lms.bootstrap.netty.ChannelAttributes;
import com.silent.lms.util.ChannelUtils;
import io.netty.channel.Channel;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author gy
 * @version 1.0
 * @date 2021/5/3
 * @description:
 */
@Log4j2
public class EventLog {
	public static final String EVENT_CLIENT_CONNECTED = "event.client-connected";
	public static final String EVENT_CLIENT_DISCONNECTED = "event.client-disconnected";

	private static final Logger logClientDisconnected = LogManager.getLogger(EVENT_CLIENT_DISCONNECTED);
	private static final Logger logClientConnected = LogManager.getLogger(EVENT_CLIENT_CONNECTED);


	public void clientWasDisconnected(@NotNull final Channel channel, @NotNull final String reason) {
		channel.attr(ChannelAttributes.DISCONNECT_EVENT_LOGGED).set(true);
		final String clientId = channel.attr(ChannelAttributes.CLIENT_ID).get();
		final String ip = ChannelUtils.getChannelIP(channel).orNull();
		log.trace("Client {} 被断开", clientId);
		logClientDisconnected.debug("Client ID: {}, IP: {} 被断开. 原因: {}.", valueOrUnknown(clientId), valueOrUnknown(ip), reason);
	}

	public void clientConnected(@NotNull final Channel channel) {
		final String clientId = channel.attr(ChannelAttributes.CLIENT_ID).get();
		final String ip = ChannelUtils.getChannelIP(channel).orNull();
		final Boolean cleanStart = channel.attr(ChannelAttributes.CLEAN_START).get();

		logClientConnected.debug("Client ID: {}, IP: {}, Clean Start: {} 已连接.", valueOrUnknown(clientId), valueOrUnknown(ip), valueOrUnknown(cleanStart));
	}

	public void clientDisconnected(@NotNull final Channel channel) {
		channel.attr(ChannelAttributes.DISCONNECT_EVENT_LOGGED).set(true);
		final String clientId = channel.attr(ChannelAttributes.CLIENT_ID).get();
		final String ip = ChannelUtils.getChannelIP(channel).orNull();
		log.trace("Client {} 断开连接", clientId);
		logClientDisconnected.debug("Client ID: {}, IP: {} 断开连接", valueOrUnknown(clientId), valueOrUnknown(ip));
	}

	@NotNull
	private String valueOrUnknown(@Nullable final Object object) {
		return object != null ? object.toString() : "UNKNOWN";
	}
}
