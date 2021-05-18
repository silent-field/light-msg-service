package com.silent.lms.mqtt.message.subscribe;

import com.silent.lms.annotations.NotNull;
import com.silent.lms.mqtt.message.QoS;

import java.io.Serializable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author gy
 * @version 1.0
 * @date 2021/5/8.
 * @description:
 */
public class Topic implements Serializable, Comparable<Topic>, Mqtt3Topic {
	/**
	 * 默认QoS级别
	 */
	public static final QoS DEFAULT_QOS = QoS.AT_LEAST_ONCE;

	@NotNull
	private String topic;
	@NotNull
	private QoS qoS;

	public Topic(final @NotNull String topic, final @NotNull QoS qoS) {
		checkNotNull(topic, "Topic不能为空");
		checkNotNull(qoS, "QoS不能为空");

		this.topic = topic;
		this.qoS = qoS;
	}

	@Override
	public @NotNull String getTopic() {
		return this.topic;
	}

	@Override
	public @NotNull QoS getQoS() {
		return this.qoS;
	}

	@Override
	public int compareTo(Topic o) {
		return this.topic.compareTo(o.getTopic());
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		final Topic another = (Topic) o;

		return topic.equals(another.topic);
	}

	@Override
	public int hashCode() {
		return topic.hashCode();
	}

	@Override
	public String toString() {
		return "Topic{" +
				"topic='" + topic + '\'' +
				", qoS=" + qoS +
				'}';
	}
}
