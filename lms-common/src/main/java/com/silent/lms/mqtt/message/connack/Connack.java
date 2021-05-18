package com.silent.lms.mqtt.message.connack;

import com.silent.lms.annotations.NotNull;
import com.silent.lms.mqtt.message.AbstractMessageWithID;
import com.silent.lms.mqtt.message.MessageType;
import com.silent.lms.mqtt.reason.MqttConnAckReasonCode;

/**
 * @author gy
 * @version 1.0
 * @date 2021/5/3
 * @description:
 */
public class Connack extends AbstractMessageWithID implements Mqtt3Connack {
    private final boolean sessionPresent;
    private final MqttConnAckReasonCode returnCode;

    public Connack(@NotNull final MqttConnAckReasonCode returnCode) {
        this(returnCode, false);
    }

    public Connack(@NotNull final MqttConnAckReasonCode returnCode, final boolean sessionPresent) {
        if (returnCode != MqttConnAckReasonCode.ACCEPTED && sessionPresent) {
            throw new IllegalArgumentException("sessionPresent标志只允许在accepted的时候有效");
        }

        this.sessionPresent = sessionPresent;
        this.returnCode = returnCode;
    }

    @Override
    public MessageType getType() {
        return MessageType.CONNACK;
    }

    @Override
    public boolean isSessionPresent() {
        return this.sessionPresent;
    }

    @Override
    public MqttConnAckReasonCode getReturnCode() {
        return this.returnCode;
    }
}
