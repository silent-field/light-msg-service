package com.silent.lms.codec.encoder.helper;

import com.silent.lms.annotations.NotNull;
import com.silent.lms.mqtt.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public abstract class AbstractVariableHeaderLengthEncoder<T extends Message> extends FixedSizeMessageEncoder<T> {

    protected ByteBuf createRemainingLength(final int messageLength, final ByteBuf buffer) {
        int val = messageLength;

        do {
            byte b = (byte) (val % 128);
            val = val / 128;
            if (val > 0) {
                b = (byte) (b | (byte) 128);
            }
            buffer.writeByte(b);
        } while (val > 0);

        return buffer;
    }

    @Override
    public int bufferSize(final @NotNull ChannelHandlerContext ctx, final @NotNull T msg) {

        final int remainingLength = remainingLength(msg);
        final int encodedLengthWithHeader = MqttMessageEncoderUtil.encodedPacketLength(remainingLength);

        msg.setRemainingLength(remainingLength);
        msg.setEncodedLength(encodedLengthWithHeader);

        return encodedLengthWithHeader;
    }

    protected abstract int remainingLength(T msg);
}
