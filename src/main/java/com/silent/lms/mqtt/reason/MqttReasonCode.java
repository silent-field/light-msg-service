package com.silent.lms.mqtt.reason;

/**
 * @author gy
 * @version 1.0
 * @date 2021/5/8.
 * @description:
 */
public interface MqttReasonCode {
	int getCode();

	default boolean isError() {
		return getCode() >= 0x80;
	}

	default boolean canBeSentByServer() {
		return true;
	}

	default boolean canBeSentByClient() {
		return false;
	}
}
