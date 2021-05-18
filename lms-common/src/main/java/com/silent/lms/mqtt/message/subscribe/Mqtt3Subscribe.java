package com.silent.lms.mqtt.message.subscribe;

import com.google.common.collect.ImmutableList;
import com.silent.lms.annotations.NotNull;

/**
 * @author gy
 * @version 1.0
 * @date 2021/5/8.
 * @description:
 */
public interface Mqtt3Subscribe {
	@NotNull
	ImmutableList<Topic> getTopics();
}
