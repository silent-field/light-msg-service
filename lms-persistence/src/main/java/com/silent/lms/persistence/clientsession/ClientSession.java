package com.silent.lms.persistence.clientsession;

import com.silent.lms.mqtt.message.connect.MqttWill;

import java.io.Serializable;

/**
 *
 * @author gy
 * @date 2021/5/14.
 * @version 1.0
 * @description: client session，用于存储
 */
public class ClientSession implements Serializable {
	private String lmsServerId;

	private String clientId;

	private String channelId;

	private int expire;

	private boolean cleanSession;

	private MqttWill mqttWill;


}
