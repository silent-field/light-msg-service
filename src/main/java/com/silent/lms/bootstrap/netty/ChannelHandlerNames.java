package com.silent.lms.bootstrap.netty;

/**
 * @author gy
 * @version 1.0
 * @date 2021/5/3
 * @description:
 */
public interface ChannelHandlerNames {
    /* *************
     *   Ingoing   *
     ***************/

    String ALL_CHANNELS_GROUP_HANDLER = "all_channel_group_handler";

    String MQTT_MESSAGE_DECODER = "mqtt_message_decoder";

    String NEW_CONNECTION_IDLE_HANDLER = "new_connection_idle_handler";

    String NO_CONNECT_IDLE_EVENT_HANDLER = "no_connect_idle_event_handler";

    String MQTT_CONNECT_HANDLER = "mqtt_connect_handler";

    String MQTT_SUBSCRIBE_HANDLER = "mqtt_subscribe_handler";

    String MQTT_PINGREQ_HANDLER = "mqtt_pingreq_handler";
    String MQTT_UNSUBSCRIBE_HANDLER = "mqtt_unsubscribe_handler";


    /* *************
     *   Outgoing  *
     ***************/

    String MQTT_MESSAGE_ENCODER = "mqtt_message_encoder";
}
