package com.silent.lms.mqtt.message.connack;

import com.silent.lms.annotations.NotNull;
import com.silent.lms.mqtt.message.AbstractMessageWithID;
import com.silent.lms.mqtt.message.MessageType;

/**
 * @author gy
 * @version 1.0
 * @date 2021/5/3
 * @description:
 */
public class Connack extends AbstractMessageWithID implements Mqtt3Connack {
    private final boolean sessionPresent;
    private final MqttConnAckReturnCode returnCode;

    public Connack(@NotNull final MqttConnAckReturnCode returnCode) {
        this(returnCode, false);
    }

    public Connack(@NotNull final MqttConnAckReturnCode returnCode, final boolean sessionPresent) {
        if (returnCode != MqttConnAckReturnCode.ACCEPTED && sessionPresent) {
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
    public MqttConnAckReturnCode getReturnCode() {
        return this.returnCode;
    }
}
