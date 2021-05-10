package com.silent.lms.bootstrap.netty;

import com.silent.lms.annotations.NotNull;
import com.silent.lms.codec.decoder.MqttDecoders;
import com.silent.lms.codec.encoder.MQTTMessageEncoder;
import com.silent.lms.mqtt.handler.connack.connect.NoConnectIdleHandler;
import com.silent.lms.mqtt.handler.connect.ConnectHandler;
import com.silent.lms.mqtt.handler.disconnect.MqttServerDisconnector;
import com.silent.lms.mqtt.handler.subscribe.SubscribeHandler;
import io.netty.channel.group.ChannelGroup;
import lombok.Getter;

import javax.inject.Inject;
import javax.inject.Provider;

/**
 * @author gy
 * @version 1.0
 * @date 2021/5/2
 * @description:
 */
public class ChannelDependencies {
    @Getter
    @NotNull
    private final ChannelGroup channelGroup;

    @Getter
    @NotNull
    private final MqttDecoders mqttDecoders;

    @Getter
    @NotNull
    private final MQTTMessageEncoder mqttMessageEncoder;

    @Getter
    @NotNull
    private final NoConnectIdleHandler noConnectIdleHandler;

    @NotNull
    private final Provider<ConnectHandler> connectHandlerProvider;

    @Getter
    @NotNull
    private final MqttServerDisconnector mqttServerDisconnector;

    @NotNull
    private final Provider<SubscribeHandler> subscribeHandlerProvider;

    @Inject
    public ChannelDependencies(final @NotNull ChannelGroup channelGroup,
                               final @NotNull MqttDecoders mqttDecoders,
                               final @NotNull MQTTMessageEncoder mqttMessageEncoder,
                               final @NotNull NoConnectIdleHandler noConnectIdleHandler,
                               final @NotNull Provider<ConnectHandler> connectHandlerProvider,
                               final @NotNull MqttServerDisconnector mqttServerDisconnector,
                               final @NotNull Provider<SubscribeHandler> subscribeHandlerProvider
    ) {
        this.channelGroup = channelGroup;
        this.mqttDecoders = mqttDecoders;
        this.mqttMessageEncoder = mqttMessageEncoder;
        this.noConnectIdleHandler = noConnectIdleHandler;
        this.connectHandlerProvider = connectHandlerProvider;
        this.mqttServerDisconnector = mqttServerDisconnector;
        this.subscribeHandlerProvider = subscribeHandlerProvider;
    }

    @NotNull
    public ConnectHandler getConnectHandler() {
        return connectHandlerProvider.get();
    }

    @NotNull
    public SubscribeHandler getSubscribeHandler() {
        return subscribeHandlerProvider.get();
    }
}
