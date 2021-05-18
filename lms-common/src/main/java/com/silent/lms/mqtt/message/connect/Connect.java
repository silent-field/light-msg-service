package com.silent.lms.mqtt.message.connect;

import com.silent.lms.annotations.NotNull;
import com.silent.lms.annotations.Nullable;
import com.silent.lms.mqtt.message.AbstractMessageWithID;
import com.silent.lms.mqtt.message.MessageType;
import com.silent.lms.mqtt.message.ProtocolVersion;
import lombok.Builder;

import java.nio.charset.StandardCharsets;

/**
 * @author gy
 * @version 1.0
 * @date 2021/5/2
 * @description:
 */
@Builder
public class Connect extends AbstractMessageWithID implements  Mqtt3Connect{
    private final @NotNull ProtocolVersion protocolVersion;
    private final @NotNull String clientId;
    private final int keepAlive;
    private final boolean cleanSession;
    private long sessionExpiryInterval;

    // 鉴权
    private final @Nullable String username;
    private final byte @Nullable [] password;

    private final @Nullable MqttWill will;

    private Connect(
            final @NotNull ProtocolVersion protocolVersion,
            final @NotNull String clientId,
            final int keepAlive,
            final boolean cleanSession,
            final long sessionExpiryInterval,
            final @Nullable String username,
            final byte @Nullable [] password,
            final @Nullable MqttWill will) {
        this.protocolVersion = protocolVersion;
        this.clientId = clientId;
        this.keepAlive = keepAlive;
        this.cleanSession = cleanSession;
        this.sessionExpiryInterval = sessionExpiryInterval;
        this.username = username;
        this.password = password;
        this.will = will;
    }

    @Override
    public MessageType getType() {
        return null;
    }

    @Override
    public @NotNull ProtocolVersion getProtocolVersion() {
        return protocolVersion;
    }

    @Override
    public @NotNull String getClientId() {
        return this.clientId;
    }

    @Override
    public int getKeepAlive() {
        return this.keepAlive;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public @Nullable byte[] getPassword() {
        return this.password;
    }

    @Override
    public String getPasswordAsUTF8String() {
        return this.password != null ? new String(this.password, StandardCharsets.UTF_8) : null;
    }

    @Override
    public MqttWill getWill() {
        return this.will;
    }
}
