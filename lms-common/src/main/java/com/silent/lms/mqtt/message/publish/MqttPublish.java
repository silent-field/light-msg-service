package com.silent.lms.mqtt.message.publish;

import com.silent.lms.mqtt.message.Message;
import com.silent.lms.mqtt.message.QoS;

public interface MqttPublish extends Message {

	/**
	 * @return the LmsServer id of the publish message
	 */
	String getLmsServerId();

	/**
	 * @return the unique id of the publish message
	 */
	String getUniqueId();

	/**
	 * @return the pulish id of the publish message
	 */
	long getPublishId();

	/**
	 * @return the payload of the publish message
	 */
	byte[] getPayload();

	/**
	 * @return the topic of the publish message
	 */
	String getTopic();

	/**
	 * @return the duplicate delivery flag of the publish message
	 */
	boolean isDuplicateDelivery();

	/**
	 * @return the retain flag of the publish message
	 */
	boolean isRetain();

	/**
	 * @return the quality of service of the publish message
	 */
	QoS getQoS();

	/**
	 * @return the timestamp of the publish message
	 */
	long getTimestamp();

	/**
	 * @return the packet identifier of the publish message
	 */
	int getPacketId();


}