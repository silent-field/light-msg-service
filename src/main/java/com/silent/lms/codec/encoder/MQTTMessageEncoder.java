package com.silent.lms.codec.encoder;

import com.google.inject.Inject;
import com.silent.lms.annotations.NotNull;
import com.silent.lms.mqtt.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author gy
 * @version 1.0
 * @date 2021/5/4
 * @description:
 */
@ChannelHandler.Sharable
public class MQTTMessageEncoder extends MessageToByteEncoder<Message> {
    private final @NotNull MqttEncoders mqttEncoders;

    @Inject
    public MQTTMessageEncoder(
            final @NotNull MqttEncoders mqttEncoders) {
        this.mqttEncoders = mqttEncoders;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
        mqttEncoders.encode(ctx, msg, out);
    }
}
