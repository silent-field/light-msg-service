package com.silent.lms.mqtt.message.unsubscribe;

import com.google.common.collect.ImmutableList;

/**
 *
 * @author gy
 * @date 2021/5/11.
 * @version 1.0
 * @description:
 */
public interface MqttUnsubscribe {
	/**
	 * 期望取消订阅的topic集合
	 * @return
	 */
	ImmutableList<String> getTopics();
}
