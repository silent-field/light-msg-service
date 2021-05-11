package com.silent.lms.codec.encoder;

import com.silent.lms.annotations.NotNull;
import com.silent.lms.annotations.Nullable;
import com.silent.lms.mqtt.handler.disconnect.MqttServerDisconnector;
import com.silent.lms.mqtt.message.Message;
import com.silent.lms.mqtt.message.connack.Connack;
import com.silent.lms.mqtt.message.pingresp.PingResp;
import com.silent.lms.mqtt.message.suback.Suback;
import com.silent.lms.mqtt.message.unsuback.Unsuback;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.log4j.Log4j2;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author gy
 * @version 1.0
 * @date 2021/5/3
 * @description:
 */
@Log4j2
@Singleton
public class MqttEncoders {
	private final @NotNull MqttConnackEncoder connackEncoder;
	//    private final @NotNull MqttPubackEncoder pubackEncoder;
//    private final @NotNull MqttPubrecEncoder pubrecEncoder;
//    private final @NotNull MqttPubrelEncoder pubrelEncoder;
//    private final @NotNull MqttPubcompEncoder pubcompEncoder;
	private final @NotNull MqttSubackEncoder subackEncoder;
    private final @NotNull MqttUnsubackEncoder unsubackEncoder;
//    private final @NotNull MqttPublishEncoder publishEncoder;
//    private final @NotNull MqttSubscribeEncoder subscribeEncoder;
//    private final @NotNull MqttUnsubscribeEncoder unsubscribeEncoder;
//    private final @NotNull MqttDisconnectEncoder disconnectEncoder;
    private final @NotNull MqttPingRespEncoder pingRespEncoder;

	@Inject
	public MqttEncoders(final @NotNull MqttServerDisconnector mqttServerDisconnector) {
		this.connackEncoder = new MqttConnackEncoder();
//        this.pubackEncoder = new MqttPubackEncoder();
//        this.pubrecEncoder = new MqttPubrecEncoder();
//        this.pubrelEncoder = new MqttPubrelEncoder();
//        this.pubcompEncoder = new MqttPubcompEncoder();
		this.subackEncoder = new MqttSubackEncoder(mqttServerDisconnector);
        this.unsubackEncoder = new MqttUnsubackEncoder();
//        this.publishEncoder = new MqttPublishEncoder();
//        this.subscribeEncoder = new MqttSubscribeEncoder();
//        this.unsubscribeEncoder = new MqttUnsubscribeEncoder();
//        this.disconnectEncoder = new MqttDisconnectEncoder();
        this.pingRespEncoder = new MqttPingRespEncoder();

	}

	private @Nullable MqttEncoder getEncoder(final @NotNull Message msg) {
		if (msg instanceof Connack) {
			return connackEncoder;
		}else if (msg instanceof Suback) {
            return subackEncoder;
        }else if (msg instanceof Unsuback) {
            return unsubackEncoder;
        } else if (msg instanceof PingResp) {
            return pingRespEncoder;
        }
//        if (msg instanceof PUBLISH) {
//            return publishEncoder;
//        }
//         else if (msg instanceof PUBACK) {
//            return pubackEncoder;
//        } else if (msg instanceof PUBREC) {
//            return pubrecEncoder;
//        } else if (msg instanceof PUBREL) {
//            return pubrelEncoder;
//        } else if (msg instanceof PUBCOMP) {
//            return pubcompEncoder;
//        } else if (msg instanceof Mqtt3CONNACK) {
//            return connackEncoder;
//        } else if (msg instanceof SUBACK) {
//            return subackEncoder;
//        } else if (msg instanceof SUBSCRIBE) {
//            return subscribeEncoder;
//        } else if (msg instanceof UNSUBSCRIBE) {
//            return unsubscribeEncoder;
//        } else if (msg instanceof DISCONNECT) {
//            return disconnectEncoder;
//        } else


		return null;
	}

	public void encode(final @NotNull ChannelHandlerContext ctx, final @NotNull Message msg, final @NotNull ByteBuf out) {

		final MqttEncoder encoder = getEncoder(msg);
		if (encoder != null) {
			encoder.encode(ctx, msg, out);
		} else {
			log.error("No encoder found for msg: {} ", msg.getType());
		}
	}
}
