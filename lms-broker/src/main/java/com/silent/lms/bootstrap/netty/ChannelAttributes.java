package com.silent.lms.bootstrap.netty;

import com.silent.lms.configuration.Listener;
import com.silent.lms.mqtt.message.ProtocolVersion;
import io.netty.util.AttributeKey;

/**
 * @author gy
 * @version 1.0
 * @date 2021/5/3
 * @description:
 */
public class ChannelAttributes {
    public static final AttributeKey<ProtocolVersion> MQTT_VERSION = AttributeKey.valueOf("MQTT.Version");
    public static final AttributeKey<String> CLIENT_ID = AttributeKey.valueOf("MQTT.ClientId");
    public static final AttributeKey<Integer> CONNECT_KEEP_ALIVE = AttributeKey.valueOf("MQTT.KeepAlive");
    public static final AttributeKey<Boolean> CLEAN_START = AttributeKey.valueOf("MQTT.CleanStart");
    public static final AttributeKey<Boolean> CONNACK_SENT = AttributeKey.valueOf("MQTT.ConnackSent");

    public static final AttributeKey<Long> CONNECT_RECEIVED_TIMESTAMP = AttributeKey.valueOf("Connect.Received.Timestamp");

    /**
     * This contains the SNI hostname sent by the client if TLS SNI is used
     */
    public static final AttributeKey<String> AUTH_USERNAME = AttributeKey.valueOf("Auth.Username");
    public static final AttributeKey<byte[]> AUTH_PASSWORD = AttributeKey.valueOf("Auth.Password");

    public static final AttributeKey<Listener> LISTENER = AttributeKey.valueOf("Listener");

    public static final AttributeKey<Boolean> DISCONNECT_EVENT_LOGGED = AttributeKey.valueOf("Disconnect.Event.Logged");

}
