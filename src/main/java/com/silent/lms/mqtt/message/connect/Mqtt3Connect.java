package com.silent.lms.mqtt.message.connect;

import com.silent.lms.annotations.NotNull;
import com.silent.lms.annotations.Nullable;
import com.silent.lms.mqtt.message.Message;
import com.silent.lms.mqtt.message.ProtocolVersion;

/**
 * @author gy
 * @version 1.0
 * @date 2021/5/2
 * @description:
 * <p>
 * 在一个网络连接上，客户端只能发送一次CONNECT报文。服务端必须将客户端发送的第二个CONNECT报文当作协议违规处理并断开客户端的连接 [MQTT-3.1.0-2]。
 * 有效载荷包含一个或多个编码的字段。包括客户端的唯一标识符，Will主题，Will消息，用户名和密码。
 * 除了客户端标识之外，其它的字段都是可选的，基于标志位来决定可变报头中是否需要包含这些字段。
 * </p>
 */
public interface Mqtt3Connect extends Message {
    @NotNull ProtocolVersion getProtocolVersion();

    @NotNull  String getClientId();

    int getKeepAlive();

    @Nullable String getUsername();

    @Nullable byte[] getPassword();

    @Nullable String getPasswordAsUTF8String();

    @Nullable MqttWill getWill();
}
