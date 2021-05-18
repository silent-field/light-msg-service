package com.silent.lms.mqtt.message.publish;

import com.google.common.base.Preconditions;
import com.silent.lms.annotations.NotNull;
import com.silent.lms.annotations.Nullable;
import com.silent.lms.mqtt.message.AbstractMessageWithID;
import com.silent.lms.mqtt.message.MessageType;
import com.silent.lms.mqtt.message.QoS;
import lombok.Getter;

import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author gy
 * @date 2021/5/12.
 * @version 1.0
 * @description:
 */
public class Publish extends AbstractMessageWithID implements MqttPublish {
	public static final int NO_PUBLISH_ID_SET = -1;

	public static final AtomicLong PUBLISH_COUNTER = new AtomicLong(1);

	@Getter
	@NotNull
	private byte[] payload;
	@Getter
	@NotNull
	private final String topic;
	@Getter
	private final boolean retain;
	@Getter
	private final boolean duplicateDelivery;
	@Getter
	@NotNull
	private final QoS qoS;
	@Getter
	private final long publishId;
	@Getter
	@NotNull
	private final String lmsServerId;
	@Getter
	@NotNull
	private final String uniqueId;
	@Getter
	private long timestamp;

	public Publish(
			@NotNull final String lmsServerId,
			@NotNull final String topic,
			@Nullable final byte[] payload,
			@NotNull final QoS qos,
			final boolean isRetain,
			final int packetIdentifier,
			final boolean isDup,
			final long publishId,
			final long timestamp) {
		Preconditions.checkNotNull(lmsServerId, "LmsServer Id 不能为空");
		Preconditions.checkNotNull(topic, "Topic 不能为空");
		Preconditions.checkNotNull(qos, "QoS 不能为空");

		this.lmsServerId = lmsServerId;
		this.topic = topic;
		this.payload = payload;
		this.qoS = qos;
		this.retain = isRetain;
		this.duplicateDelivery = isDup;

		if (publishId > NO_PUBLISH_ID_SET) {
			this.publishId = publishId;
		} else {
			this.publishId = PUBLISH_COUNTER.getAndIncrement();
		}
		this.uniqueId = lmsServerId + "_pub_" + this.publishId;

		if (timestamp > -1) {
			this.timestamp = timestamp;
		} else {
			this.timestamp = System.currentTimeMillis();
		}

		setPacketId(packetIdentifier);
	}

	@Override
	public MessageType getType() {
		return MessageType.PUBLISH;
	}

}
