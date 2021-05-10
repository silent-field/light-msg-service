package com.silent.lms.codec.decoder;

import com.google.inject.Inject;
import com.silent.lms.annotations.NotNull;
import com.silent.lms.mqtt.handler.connack.MqttConnacker;
import com.silent.lms.mqtt.message.ProtocolVersion;
import com.silent.lms.mqtt.message.QoS;
import com.silent.lms.mqtt.message.connack.MqttConnAckReturnCode;
import com.silent.lms.mqtt.message.connect.Connect;
import com.silent.lms.mqtt.message.connect.MqttWill;
import com.silent.lms.bootstrap.netty.ChannelAttributes;
import com.silent.lms.util.Bytes;
import com.silent.lms.util.Strings;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import static com.silent.lms.util.Bytes.isBitSet;

/**
 * @author gy
 * @version 1.0
 * @date 2021/5/3
 * @description:
 * <p>
 *  固定报头 Fixed header
 *  byte 1      0001 0000(协议类型：CONNECT)
 *  byte 2...   剩余长度等于可变报头的长度（10字节）加上有效载荷的长度
 * <p>
 *  可变报头 Variable header
 *  CONNECT报文的可变报头按下列次序包含四个字段
 *  协议名（Protocol Name）：
 *      byte1(0) 0000 0000;byte2(4) 0000 0100;
 *      byte3(M) 0100 1101;byte4(Q) 0101 0001;byte5(T) 0101 0100;byte6(T) 0101 0100
 *  协议级别（Protocol Level）：4表示v3.1.1版协议
 *      byte7(4) 0000 0100
 *  连接标志（Connect Flags），连接标志字节包含一些用于指定MQTT连接行为的参数。它还指出有效载荷中的字段是否存在。
 *      byte8
 *      bit7 user name flag：如果用户名（User Name）标志被设置为0，有效载荷中不能包含用户名字段 [MQTT-3.1.2-18]。
 *                          如果用户名（User Name）标志被设置为1，有效载荷中必须包含用户名字段 [MQTT-3.1.2-19]。
 *      bit6 password flag：如果密码（Password）标志被设置为0，有效载荷中不能包含密码字段 [MQTT-3.1.2-20]。
 *                          如果密码（Password）标志被设置为1，有效载荷中必须包含密码字段 [MQTT-3.1.2-21]。
 *      bit5 will retain：如果遗嘱消息被发布时需要保留，需要指定这一位的值。
 *                          如果遗嘱标志被设置为0，遗嘱保留（Will Retain）标志也必须设置为0 [MQTT-3.1.2-15]。
 *                          如果遗嘱标志被设置为1：
 *                          如果遗嘱保留被设置为0，服务端必须将遗嘱消息当作非保留消息发布 [MQTT-3.1.2-16]。
 *                          如果遗嘱保留被设置为1，服务端必须将遗嘱消息当作保留消息发布 [MQTT-3.1.2-17]。
 *      bit4/3 Will QoS：这两位用于指定发布遗嘱消息时使用的服务质量等级。
 *                      如果遗嘱标志被设置为0，遗嘱QoS也必须设置为0(0x00) [MQTT-3.1.2-13]。
 *                      如果遗嘱标志被设置为1，遗嘱QoS的值可以等于0(0x00)，1(0x01)，2(0x02)。它的值不能等于3 [MQTT-3.1.2-14]。
 *      bit2 Will flag：遗嘱标志（Will Flag）被设置为1，表示如果连接请求被接受了，遗嘱（Will Message）消息必须被存储在服务端并且与这个网络连接关联。
 *                      之后网络连接关闭时，服务端必须发布这个遗嘱消息，除非服务端收到DISCONNECT报文时删除了这个遗嘱消息 [MQTT-3.1.2-8] 。
 *      bit1 Clean Session：设置为0，服务端必须基于当前会话（使用客户端标识符识别）的状态恢复与客户端的通信。
 *      bit0 0：服务端必须验证CONNECT控制报文的保留标志位（第0位）是否为0，如果不为0必须断开客户端连接 [MQTT-3.1.2-3]
 *
 *  保持连接（Keep Alive）
 *      16位，是一个以秒为单位的时间间隔。byte9~10
 * <p>
 *  有效载荷 Payload
 *  客户端标识符 Client Identifier：1到23个字节长的UTF-8编码的客户端标识符
 *  遗嘱主题 Will Topic：如果遗嘱标志被设置为1，有效载荷的下一个字段是遗嘱主题（Will Topic）。
 *  遗嘱消息 Will Message：如果遗嘱标志被设置为1，有效载荷的下一个字段是遗嘱消息。遗嘱消息定义了将被发布到遗嘱主题的应用消息，
 *                      这个字段由一个两字节的长度和遗嘱消息的有效载荷组成，表示为零字节或多个字节序列。
 *                      长度给出了跟在后面的数据的字节数，不包含长度字段本身占用的两个字节。
 *  用户名 User Name：如果用户名（User Name）标志被设置为1，有效载荷的下一个字段就是它。
 *  密码 Password：如果密码（Password）标志被设置为1，有效载荷的下一个字段就是它。
 *              密码字段包含一个两字节的长度字段，长度表示二进制数据的字节数（不包含长度字段本身占用的两个字节），后面跟着0到65535字节的二进制数据。
 */
public class MqttConnectDecoder extends MqttDecoder<Connect> {
    private static final byte VARIABLE_HEADER_LENGTH = 10;

    public static final String PROTOCOL_NAME = "MQTT";

    private final @NotNull MqttConnacker mqttConnacker;

    @Inject
    public MqttConnectDecoder(final @NotNull MqttConnacker mqttConnacker) {
        this.mqttConnacker = mqttConnacker;
    }

    @Override
    public Connect decode(Channel channel, ByteBuf buf, byte header) {
        if (buf.readableBytes() < 2) {
            mqttConnacker.connackError(channel,
                    "client (IP: {}) CONNECT 包没有协议版本号",
                    "CONNECT没有协议版本",
                    MqttConnAckReturnCode.REFUSED_UNACCEPTABLE_PROTOCOL_VERSION);
            return null;
        }

        // 可变报头第二个字节值表示协议名
        final ByteBuf lengthLSBBuf = buf.slice(buf.readerIndex() + 1, 1);

        final int lengthLSB = lengthLSBBuf.readByte();

        final ProtocolVersion protocolVersion;

        switch (lengthLSB) {
            case 4:
                // 协议名（Protocol Name）占用6个字节，第7个字节值表示协议版本
                if (buf.readableBytes() < 7) {
                    connackInvalidProtocolVersion(channel);
                    return null;
                }
                final ByteBuf protocolVersionBuf = buf.slice(buf.readerIndex() + 6, 1);
                final byte versionByte = protocolVersionBuf.readByte();
                if (versionByte == 4) {
                    protocolVersion = ProtocolVersion.MQTTv3_1_1;
                } else {
                    connackInvalidProtocolVersion(channel);
                    return null;
                }
                break;
            default:
                connackInvalidProtocolVersion(channel);
                return null;
        }

        channel.attr(ChannelAttributes.MQTT_VERSION).set(protocolVersion);
        channel.attr(ChannelAttributes.CONNECT_RECEIVED_TIMESTAMP).set(System.currentTimeMillis());

        // decode 构建Connect
        return innerDecode(channel, buf, header);
    }

    private Connect innerDecode(final @NotNull Channel channel, final @NotNull ByteBuf buf, final byte header) {
        if (!validateHeader(header)) {
            disconnectByInvalidFixedHeader(channel);
            return null;
        }

        /**可变报头*/
        final ByteBuf connectHeader = decodeFixedVariableHeaderConnect(channel, buf);
        if (connectHeader == null) {
            return null;
        }

        if (!validateProtocolName(connectHeader, channel, PROTOCOL_NAME)) {
            return null;
        }

        // 跳过协议级别，之前已经读过
        connectHeader.readByte();

        // 连接标志
        final byte connectFlagsByte = connectHeader.readByte();

        if (!validateConnectFlagByte(connectFlagsByte, channel)) {
            return null;
        }

        final boolean isCleanSessionFlag = isBitSet(connectFlagsByte, 1);
        final boolean isWillFlag = isBitSet(connectFlagsByte, 2);
        final int willQoS = (connectFlagsByte & 0b0001_1000) >> 3;
        final boolean isWillRetain = isBitSet(connectFlagsByte, 5);
        final boolean isPasswordFlag = isBitSet(connectFlagsByte, 6);
        final boolean isUsernameFlag = isBitSet(connectFlagsByte, 7);

        if (!validateWill(isWillFlag, isWillRetain, willQoS, channel)) {
            return null;
        }

        if (!validateUsernamePassword(isUsernameFlag, isPasswordFlag, channel)) {
            return null;
        }

        // keepAlive值
        final int keepAlive = connectHeader.readUnsignedShort();

        // 有效载荷 Payload
        final String clientId = validateClientId(buf, channel);
        channel.attr(ChannelAttributes.CLIENT_ID).set(clientId);

        final MqttWill will;
        if (isWillFlag) {
            will = readMqttWill(channel, buf, willQoS, isWillRetain);
            //channel already closed.
            if (will == null) {
                return null;
            }
        } else {
            will = null;
        }

        Pair<String, byte[]> usernamePasswordPair = readUsernamePassword(isUsernameFlag, isPasswordFlag, buf, channel);

        channel.attr(ChannelAttributes.CONNECT_KEEP_ALIVE).set(keepAlive);
        channel.attr(ChannelAttributes.CLEAN_START).set(isCleanSessionFlag);

        return Connect.builder().protocolVersion(ProtocolVersion.MQTTv3_1_1)
                .clientId(clientId)
                .username(usernamePasswordPair.getLeft())
                .password(usernamePasswordPair.getRight())
                .cleanStart(isCleanSessionFlag)
                .sessionExpiryInterval(isCleanSessionFlag ? 0 : 6 * 60 * 60)
                .keepAlive(keepAlive)
                .will(will).build();
    }

    protected ByteBuf decodeFixedVariableHeaderConnect(final @NotNull Channel channel, final @NotNull ByteBuf buf) {
        if (buf.readableBytes() >= VARIABLE_HEADER_LENGTH) {
            return buf.readSlice(VARIABLE_HEADER_LENGTH);
        } else {
            disconnectByInvalidHeader(channel);
            return null;
        }
    }

    private void connackInvalidProtocolVersion(final @NotNull Channel channel) {
        mqttConnacker.connackError(channel,
                "client (IP: {}) CONNECT 版本不支持.",
                "CONNECT 版本不支持",
                MqttConnAckReturnCode.REFUSED_UNACCEPTABLE_PROTOCOL_VERSION);
    }

    protected void disconnectByInvalidFixedHeader(final @NotNull Channel channel) {
        mqttConnacker.connackError(channel,
                "client (IP: {}) CONNECT 报文fixed header格式不合法.",
                "CONNECT fixed header不合法",
                MqttConnAckReturnCode.REFUSED_NOT_AUTHORIZED);
    }

    protected void disconnectByInvalidHeader(final @NotNull Channel channel) {
        mqttConnacker.connackError(channel,
                "client (IP: {}) CONNECT header不合法",
                "CONNECT header不合法",
                MqttConnAckReturnCode.REFUSED_NOT_AUTHORIZED);
    }

    protected boolean validateProtocolName(final @NotNull ByteBuf variableHeader, final @NotNull Channel channel, final @NotNull String protocolName) {

        if (!protocolName.equals(Strings.getPrefixedString(variableHeader))) {
            mqttConnacker.connackError(
                    channel,
                    "client (IP: {}) CONNECT 协议名不合法",
                    "CONNECT 协议名不合法",
                    MqttConnAckReturnCode.REFUSED_UNACCEPTABLE_PROTOCOL_VERSION);
            variableHeader.clear();
            return false;
        }
        return true;
    }

    protected boolean validateConnectFlagByte(final byte connectFlagsByte, final @NotNull Channel channel) {
        /**
         *  连接标志（Connect Flags），连接标志字节包含一些用于指定MQTT连接行为的参数。它还指出有效载荷中的字段是否存在。
         *      bit0 0：服务端必须验证CONNECT控制报文的保留标志位（第0位）是否为0，如果不为0必须断开客户端连接 [MQTT-3.1.2-3]
         */
        if (isBitSet(connectFlagsByte, 0)) {
            mqttConnacker.connackError(
                    channel,
                    "client (IP: {}) CONNECT flags不合法",
                    "CONNECT flags不合法",
                    MqttConnAckReturnCode.REFUSED_NOT_AUTHORIZED);
            return false;
        }
        return true;
    }

    protected boolean validateWill(final boolean isWillFlag, final boolean isWillRetain, final int willQoS, final @NotNull Channel channel) {
        /**
         *  连接标志（Connect Flags），连接标志字节包含一些用于指定MQTT连接行为的参数。它还指出有效载荷中的字段是否存在。
         *      bit5 will retain：如果遗嘱消息被发布时需要保留，需要指定这一位的值。
         *      bit4/3 Will QoS：这两位用于指定发布遗嘱消息时使用的服务质量等级。
         *      bit2 Will flag：遗嘱标志（Will Flag）被设置为1，表示如果连接请求被接受了，遗嘱（Will Message）消息必须被存储在服务端并且与这个网络连接关联。
         */
        final boolean valid = (isWillFlag && willQoS < 3) || (!isWillRetain && willQoS == 0);
        if (!valid) {
            mqttConnacker.connackError(channel, "client (IP: {}) willTopic flag 值不合法",
                    "will-topic/flag 值不合法",
                    MqttConnAckReturnCode.REFUSED_NOT_AUTHORIZED);
        }
        return valid;
    }

    protected boolean validateUsernamePassword(final boolean isUsernameFlag, final boolean isPasswordFlag, final @NotNull Channel channel) {
        boolean valid = !isPasswordFlag || isUsernameFlag;

        if (!valid) {
            mqttConnacker.connackError(channel,
                    "client (IP: {}) CONNECT username/password 不合法.",
                    "CONNECT username/password 不合法",
                    MqttConnAckReturnCode.REFUSED_NOT_AUTHORIZED);
        }
        return valid;
    }

    protected String validateClientId(final @NotNull ByteBuf buf, final @NotNull Channel channel) {
        final int utf8StringLength;

        if (buf.readableBytes() < 2 || ((utf8StringLength = buf.readUnsignedShort()) <= 0) || (buf.readableBytes() < utf8StringLength)) {
            mqttConnacker.connackError(channel,
                    "client (IP: {}) CONNECT clientId长度不合法",
                    "CONNECT clientId长度不合法",
                    MqttConnAckReturnCode.REFUSED_NOT_AUTHORIZED);
            return null;
        }

        final String clientId = Strings.getValidatedPrefixedString(buf, utf8StringLength, true);
        if (clientId == null) {
            mqttConnacker.connackError(channel,
                    "client (IP: {}) CONNECT clientId 格式不合法",
                    "CONNECT clientId 格式不合法",
                    MqttConnAckReturnCode.REFUSED_NOT_AUTHORIZED);
            return null;
        }

        return clientId;
    }

    protected MqttWill readMqttWill(final @NotNull Channel channel,
                                    final @NotNull ByteBuf buf,
                                    final int willQoS,
                                    final boolean isWillRetain) {
        final MqttWill.Builder willBuilder = new MqttWill.Builder();
        willBuilder.withQos(QoS.getByCode(willQoS));
        willBuilder.withRetain(isWillRetain);

        final String willTopic;
        final int utf8StringLengthWill;

        if (buf.readableBytes() < 2 || buf.readableBytes() < (utf8StringLengthWill = buf.readUnsignedShort())) {
            mqttConnacker.connackError(
                    channel,
                    "client (IP: {}) CONNECT will-topic 长度不合法",
                    "CONNECT will-topic 长度不合法",
                    MqttConnAckReturnCode.REFUSED_NOT_AUTHORIZED);
            return null;
        }

        willTopic = Strings.getValidatedPrefixedString(buf, utf8StringLengthWill, true);
        if (willTopic == null) {
            mqttConnacker.connackError(
                    channel,
                    "client (IP: {}) will-topic格式不合法",
                    "will-topic格式不合法",
                    MqttConnAckReturnCode.REFUSED_NOT_AUTHORIZED);
            return null;
        }

        if (isInvalidTopic(channel, willTopic)) {
            mqttConnacker.connackError(
                    channel,
                    null, //already logged
                    "will-topic格式不合法",
                    MqttConnAckReturnCode.REFUSED_NOT_AUTHORIZED);
            return null;
        }

        final byte[] prefixedBytes = Bytes.getPrefixedBytes(buf);
        final byte[] willMessage = prefixedBytes != null ? prefixedBytes : new byte[0];

        return willBuilder.withPayload(willMessage).withTopic(willTopic).build();
    }

    protected Pair<String, byte[]> readUsernamePassword(final boolean isUsernameFlag, final boolean isPasswordFlag, ByteBuf buf, final @NotNull Channel channel) {
        final String userName;

        if (isUsernameFlag) {
            userName = Strings.getPrefixedString(buf);
            if (userName == null) {
                mqttConnacker.connackError(channel,
                        "client (IP: {}) CONNECT username 长度不合法",
                        "CONNECT username 长度不合法",
                        MqttConnAckReturnCode.REFUSED_NOT_AUTHORIZED);
                return null;
            }
            channel.attr(ChannelAttributes.AUTH_USERNAME).set(userName);
        } else {
            userName = null;
        }

        final byte[] password;

        if (isPasswordFlag) {
            password = Bytes.getPrefixedBytes(buf);
            if (password == null) {
                mqttConnacker.connackError(channel,
                        "client (IP: {}) CONNECT password 长度不合法",
                        "CONNECT password 长度不合法",
                        MqttConnAckReturnCode.REFUSED_NOT_AUTHORIZED);
                return null;
            }
            channel.attr(ChannelAttributes.AUTH_PASSWORD).set(password);
        } else {
            password = null;
        }

        return ImmutablePair.of(userName, password);
    }
}
