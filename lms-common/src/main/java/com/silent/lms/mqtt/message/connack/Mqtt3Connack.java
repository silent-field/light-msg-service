package com.silent.lms.mqtt.message.connack;

import com.silent.lms.mqtt.message.Message;
import com.silent.lms.mqtt.reason.MqttConnAckReasonCode;

public interface Mqtt3Connack extends Message {
    boolean isSessionPresent();

    MqttConnAckReasonCode getReturnCode();
}