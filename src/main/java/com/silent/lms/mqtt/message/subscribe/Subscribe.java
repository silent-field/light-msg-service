package com.silent.lms.mqtt.message.subscribe;

import com.google.common.collect.ImmutableList;
import com.silent.lms.annotations.NotNull;
import com.silent.lms.mqtt.message.AbstractMessageWithID;
import com.silent.lms.mqtt.message.MessageType;

/**
 * @author gy
 * @version 1.0
 * @date 2021/5/8.
 * @description:
 */
public class Subscribe extends AbstractMessageWithID implements Mqtt3Subscribe {
	@NotNull
	private final ImmutableList<Topic> topics;

	public Subscribe(
			final @NotNull ImmutableList<Topic> topics,
			final int packetId) {
		this.topics = topics;
		super.setPacketId(packetId);
	}

	@Override
	public MessageType getType() {
		return MessageType.SUBSCRIBE;
	}

	@Override
	public @NotNull ImmutableList<Topic> getTopics() {
		return topics;
	}
}
