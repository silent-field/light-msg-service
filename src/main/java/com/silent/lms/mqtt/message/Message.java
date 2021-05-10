package com.silent.lms.mqtt.message;

import java.io.Serializable;

/**
 * @author gy
 * @version 1.0
 * @date 2021/4/30.
 * @description:
 */
public interface Message extends Serializable {
	MessageType getType();

	/**
	 * 编码后的协议消息字节长度
	 */
	void setEncodedLength(final int length);

	int getEncodedLength();

	/**
	 * MQTT控制协议消息的剩余长度
	 */
	void setRemainingLength(final int length);

	int getRemainingLength();

	/**
	 * 协议消息属性长度
	 */
	void setPropertyLength(final int length);

	int getPropertyLength();

	void setOmittedProperties(final int length);

	int getOmittedProperties();
}
