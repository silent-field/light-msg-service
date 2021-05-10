package com.silent.lms.mqtt.message.suback;

import com.google.common.collect.ImmutableList;
import com.silent.lms.mqtt.reason.MqttSubAckReasonCode;

public interface Mqtt3SUBACK {
    ImmutableList<MqttSubAckReasonCode> getReasonCodes();

    int getPacketId();
}