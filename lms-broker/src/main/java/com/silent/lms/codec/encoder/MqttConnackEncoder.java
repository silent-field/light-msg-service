package com.silent.lms.codec.encoder;

import com.silent.lms.annotations.NotNull;
import com.silent.lms.codec.encoder.helper.FixedSizeMessageEncoder;
import com.silent.lms.mqtt.message.connack.Connack;
import com.silent.lms.mqtt.message.connect.Connect;
import com.silent.lms.mqtt.reason.MqttConnAckReasonCode;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

/**
 * @author gy
 * @version 1.0
 * @date 2021/5/4
 * @description:
 * 服务端必须发送返回码为零的CONNACK报文作为CONNECT报文的确认响应 [MQTT-3.1.4-4]。
 * <p>
 *  固定报头
 *  byte1 0010 0000
 *  byte2 0000 0010
 * <p>
 *  可变报头
 *  byte1 0000 000X     第1位是当前会话（Session Present）标志。
 *  byte2 连接返回码
 *  0       0x00连接已接受                           连接已被服务端接受
 *  1       0x01连接已拒绝，不支持的协议版本          服务端不支持客户端请求的MQTT协议级别
 *  2       0x02连接已拒绝，不合格的客户端标识符        客户端标识符是正确的UTF-8编码，但服务端不允许使用
 *  3       0x03连接已拒绝，服务端不可用                网络连接已建立，但MQTT服务不可用
 *  4       0x04连接已拒绝，无效的用户名或密码         用户名或密码的数据格式无效
 *  5       0x05连接已拒绝，未授权                   客户端未被授权连接到此服务器
 *  6-255   保留
 *
 *  如果认为上表中的所有连接返回码都不太合适，那么服务端必须关闭网络连接，不需要发送CONNACK报文 [MQTT-3.2.2-6]。
 */
public class MqttConnackEncoder extends FixedSizeMessageEncoder<Connack> implements MqttEncoder<Connack> {
    private static final byte CONNACK_FIXED_HEADER = 0b0010_0000;
    private static final byte CONNACK_REMAINING_LENGTH = 0b0000_0010;
    private static final byte CONNACK_FLAGS_EMPTY = 0b0000_0000;
    private static final byte CONNACK_FLAGS_SP_SET = 0b0000_0001;

    private static final int ENCODED_CONNACK_SIZE = 4;

    @Override
    public void encode(ChannelHandlerContext ctx, Connack msg, ByteBuf out) {
        out.writeByte(CONNACK_FIXED_HEADER);
        out.writeByte(CONNACK_REMAINING_LENGTH);

        final MqttConnAckReasonCode returnCode = msg.getReturnCode();
        if (returnCode == MqttConnAckReasonCode.ACCEPTED && msg.isSessionPresent()) {
            out.writeByte(CONNACK_FLAGS_SP_SET);
        } else {
            out.writeByte(CONNACK_FLAGS_EMPTY);
        }
        out.writeByte(returnCode.getCode());
    }

    @Override
    public void write(final @NotNull ChannelHandlerContext ctx, final @NotNull Object msg, final @NotNull ChannelPromise promise) throws Exception {
        super.write(ctx, msg, promise);
        if (msg instanceof Connect) {
            //We make sure we really disconnect the client when there's a wrong return code
            if (((Connack) msg).getReturnCode() != MqttConnAckReasonCode.ACCEPTED) {
                promise.addListener(ChannelFutureListener.CLOSE);
            }
        }
    }

    @Override
    public int bufferSize(ChannelHandlerContext ctx, Connack msg) {
        return ENCODED_CONNACK_SIZE;
    }
}
