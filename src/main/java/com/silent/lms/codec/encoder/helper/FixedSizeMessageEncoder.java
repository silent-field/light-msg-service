package com.silent.lms.codec.encoder.helper;

import com.silent.lms.annotations.NotNull;
import com.silent.lms.mqtt.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public abstract class FixedSizeMessageEncoder<T extends Message> extends MessageToByteEncoder<T> {
    @Override
    protected @NotNull ByteBuf allocateBuffer(final ChannelHandlerContext ctx, final T msg, final boolean preferDirect) {
        final int bufferSize = bufferSize(ctx, msg);
        if (preferDirect) {
            return ctx.alloc().ioBuffer(bufferSize);
        } else {
            return ctx.alloc().heapBuffer(bufferSize);
        }
    }

    public abstract int bufferSize(@NotNull ChannelHandlerContext ctx, @NotNull T msg);
}