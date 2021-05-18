package com.silent.lms.mqtt.message.subscribe;

import com.silent.lms.annotations.NotNull;
import com.silent.lms.mqtt.message.QoS;

/**
 * @author gy
 * @version 1.0
 * @date 2021/5/8.
 * @description:
 */
public interface Mqtt3Topic {
	@NotNull
	String getTopic();

	@NotNull
	QoS getQoS();
}
