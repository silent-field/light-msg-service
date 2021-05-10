package com.silent.lms.mqtt.handler;

import com.silent.lms.annotations.NotNull;

/**
 * @author gy
 * @version 1.0
 * @date 2021/5/6.
 * @description:
 */
public interface MessageHandler<T> {
	void defaultNotSetValues(final @NotNull T message);
}
