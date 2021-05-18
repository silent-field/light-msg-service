package com.silent.lms.codec.encoder;

import com.silent.lms.annotations.NotNull;
import com.silent.lms.mqtt.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author gy
 * @version 1.0
 * @date 2021/5/4
 * @description:
 */
public interface MqttEncoder<T extends Message> {
    void encode(@NotNull ChannelHandlerContext ctx, @NotNull T msg, @NotNull ByteBuf out);

    int bufferSize(@NotNull ChannelHandlerContext ctx, @NotNull T msg);

}
