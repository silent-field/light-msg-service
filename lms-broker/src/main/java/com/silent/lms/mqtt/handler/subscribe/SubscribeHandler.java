package com.silent.lms.mqtt.handler.subscribe;

import com.silent.lms.mqtt.message.suback.Suback;
import com.silent.lms.mqtt.message.subscribe.Subscribe;
import com.silent.lms.mqtt.message.subscribe.Topic;
import com.silent.lms.mqtt.reason.MqttSubAckReasonCode;
import com.silent.lms.persistence.SubscribeHolder;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import javax.inject.Singleton;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *
 * @author gy
 * @date 2021/5/8.
 * @version 1.0
 * @description:
 */
@Singleton
@ChannelHandler.Sharable
public class SubscribeHandler extends SimpleChannelInboundHandler<Subscribe> {
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Subscribe msg) throws Exception {
		SubscribeHolder subscribeHolder = SubscribeHolder.instance;
		subscribeHolder.subscribe(ctx.channel(), msg);

		List<MqttSubAckReasonCode> reasonCodes = msg.getTopics().stream().map(new Function<Topic, MqttSubAckReasonCode>() {
			@Override
			public MqttSubAckReasonCode apply(Topic topic) {
				Integer qosLevel = topic.getQoS().getCode();
				MqttSubAckReasonCode code = MqttSubAckReasonCode.getByCode(qosLevel);
				return code;
			}
		}).collect(Collectors.toList());
		ctx.writeAndFlush(new Suback(msg.getPacketId(), reasonCodes));
	}
}
