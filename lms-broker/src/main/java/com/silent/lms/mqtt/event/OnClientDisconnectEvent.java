package com.silent.lms.mqtt.event;


import com.silent.lms.annotations.Nullable;
import com.silent.lms.mqtt.reason.MqttDisconnectReasonCode;

public class OnClientDisconnectEvent extends EventWithReasonCode{
    private final boolean graceful;

    public OnClientDisconnectEvent(
            final @Nullable MqttDisconnectReasonCode reasonCode,
            final @Nullable String reasonString,
            final boolean graceful) {
        super(reasonCode,reasonString);
        this.graceful = graceful;
    }

    public boolean isGraceful() {
        return graceful;
    }
}
