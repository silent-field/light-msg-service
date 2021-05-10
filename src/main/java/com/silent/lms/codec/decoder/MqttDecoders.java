package com.silent.lms.codec.decoder;

import com.silent.lms.annotations.NotNull;
import com.silent.lms.annotations.Nullable;
import com.silent.lms.mqtt.message.MessageType;

import javax.inject.Inject;

/**
 * @author gy
 * @version 1.0
 * @date 2021/5/3
 * @description:
 */
public class MqttDecoders {
    private final @Nullable MqttDecoder @NotNull [] mqttDecoder;

    @Inject
    public MqttDecoders(final @NotNull MqttConnectDecoder mqttConnectDecoder,
//                        final @NotNull MqttPublishDecoder mqttPublishDecoder,
//                        final @NotNull MqttPubackDecoder mqttPubackDecoder,
//                        final @NotNull MqttPubrecDecoder mqttPubrecDecoder,
//                        final @NotNull MqttPubcompDecoder mqttPubcompDecoder,
//                        final @NotNull MqttPubrelDecoder mqttPubrelDecoder,
//                        final @NotNull MqttDisconnectDecoder mqttDisconnectDecoder,
                        final @NotNull MqttSubscribeDecoder mqttSubscribeDecoder
//                        final @NotNull MqttUnsubscribeDecoder mqttUnsubscribeDecoder,
//                        final @NotNull MqttSubackDecoder mqttSubackDecoder,
//                        final @NotNull MqttUnsubackDecoder mqttUnsubackDecoder,
//                        final @NotNull MqttPingreqDecoder mqttPingreqDecoder
    ) {

        mqttDecoder = new MqttDecoder[16];

        mqttDecoder[MessageType.CONNECT.getCode()] = mqttConnectDecoder;
//        mqttDecoder[MessageType.PUBLISH.getCode()] = mqttPublishDecoder;
//        mqttDecoder[MessageType.PUBACK.getCode()] = mqttPubackDecoder;
//        mqttDecoder[MessageType.PUBREC.getCode()] = mqttPubrecDecoder;
//        mqttDecoder[MessageType.PUBREL.getCode()] = mqttPubrelDecoder;
//        mqttDecoder[MessageType.PUBCOMP.getCode()] = mqttPubcompDecoder;
        mqttDecoder[MessageType.SUBSCRIBE.getCode()] =  mqttSubscribeDecoder;
//        mqttDecoder[MessageType.UNSUBSCRIBE.getCode()] = mqttUnsubscribeDecoder;
//        mqttDecoder[MessageType.PINGREQ.getCode()] = mqttPingreqDecoder;
//        mqttDecoder[MessageType.DISCONNECT.getCode()] = mqttDisconnectDecoder;

    }

    public @Nullable MqttDecoder<?> getDecoder(final @NotNull MessageType type) {
        return mqttDecoder[type.getCode()];
    }
}
