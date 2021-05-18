package com.silent.lms.mqtt.handler.unsubscribe;

import com.silent.lms.bootstrap.netty.ChannelAttributes;
import com.silent.lms.mqtt.message.subscribe.Topic;
import com.silent.lms.mqtt.message.unsuback.Unsuback;
import com.silent.lms.mqtt.message.unsubscribe.Unsubscribe;
import com.silent.lms.mqtt.reason.MqttSubAckReasonCode;
import com.silent.lms.mqtt.reason.MqttUnsubAckReasonCode;
import com.silent.lms.persistence.SubscribeHolder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.log4j.Log4j2;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *
 * @author gy
 * @date 2021/5/11.
 * @version 1.0
 * @description:
 */
@Log4j2
public class UnsubscribeHandler extends SimpleChannelInboundHandler<Unsubscribe> {
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Unsubscribe msg) throws Exception {
		SubscribeHolder subscribeHolder = SubscribeHolder.instance;
		subscribeHolder.unsubscribe(ctx.channel(), msg);

		List<MqttUnsubAckReasonCode> reasonCodes = msg.getTopics().stream().map(new Function<String, MqttUnsubAckReasonCode>() {
			@Override
			public MqttUnsubAckReasonCode apply(String topic) {
				return MqttUnsubAckReasonCode.SUCCESS;
			}
		}).collect(Collectors.toList());

		ctx.writeAndFlush(new Unsuback(msg.getPacketId(), reasonCodes));
	}
}
