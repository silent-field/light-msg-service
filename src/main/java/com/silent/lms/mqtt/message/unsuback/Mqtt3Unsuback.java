package com.silent.lms.mqtt.message.unsuback;

import com.google.common.collect.ImmutableList;
import com.silent.lms.mqtt.reason.MqttSubAckReasonCode;
import com.silent.lms.mqtt.reason.MqttUnsubAckReasonCode;

public interface Mqtt3Unsuback {
    ImmutableList<MqttUnsubAckReasonCode> getReasonCodes();

    int getPacketId();
}