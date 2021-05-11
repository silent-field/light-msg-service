package com.silent.lms.mqtt.message.unsubscribe;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.silent.lms.annotations.NotNull;
import com.silent.lms.mqtt.message.AbstractMessageWithID;
import com.silent.lms.mqtt.message.MessageType;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 *
 * @author gy
 * @date 2021/5/11.
 * @version 1.0
 * @description:
 */
public class Unsubscribe extends AbstractMessageWithID implements MqttUnsubscribe {
	private final ImmutableList<String> topics;

	public Unsubscribe(@NotNull final List<String> topics, final int packetId) {
		Preconditions.checkArgument(!CollectionUtils.isEmpty(topics), "topics不能为空");
		this.topics = ImmutableList.copyOf(topics);
		setPacketId(packetId);
	}

	@Override
	public ImmutableList<String> getTopics() {
		return topics;
	}

	@Override
	public MessageType getType() {
		return MessageType.UNSUBSCRIBE;
	}
}
