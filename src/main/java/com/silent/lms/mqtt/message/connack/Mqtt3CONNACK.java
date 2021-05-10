package com.silent.lms.mqtt.message.connack;

import com.silent.lms.mqtt.message.Message;

public interface Mqtt3CONNACK extends Message {
    boolean isSessionPresent();

    MqttConnAckReturnCode getReturnCode();
}