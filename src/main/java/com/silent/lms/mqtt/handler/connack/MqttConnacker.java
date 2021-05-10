package com.silent.lms.mqtt.handler.connack;

import com.silent.lms.annotations.NotNull;
import com.silent.lms.annotations.Nullable;
import com.silent.lms.mqtt.message.connack.Connack;
import com.silent.lms.mqtt.message.connack.MqttConnAckReturnCode;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author gy
 * @version 1.0
 * @date 2021/5/3
 * @description:
 */
public interface MqttConnacker {
    @NotNull
    ChannelFuture connackSuccess(@NotNull ChannelHandlerContext ctx, @NotNull Connack connack);

    void connackError(
            @NotNull Channel channel,
            @Nullable String logMessage,
            @Nullable String eventLogMessage,
            @Nullable MqttConnAckReturnCode reasonCode);
}
