package com.silent.lms.mqtt.handler.publish;

import com.silent.lms.annotations.NotNull;
import com.silent.lms.bootstrap.netty.ChannelAttributes;
import com.silent.lms.mqtt.message.MessageIDPoolProviders;
import com.silent.lms.mqtt.message.publish.Publish;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.log4j.Log4j2;

import javax.inject.Inject;

/**
 *
 * @author gy
 * @date 2021/5/12.
 * @version 1.0
 * @description:
 */
@Log4j2
public class PublishFlowHandler extends ChannelDuplexHandler {
	@NotNull
	private final MessageIDPoolProviders messageIDPoolProviders;

	@Inject
	public PublishFlowHandler() {
		this.messageIDPoolProviders = MessageIDPoolProviders.instance;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		final String client = ctx.channel().attr(ChannelAttributes.CLIENT_ID).get();
		if (msg instanceof Publish) {
			handlePublish(ctx, (Publish) msg, client);
		} else {
			super.channelRead(ctx, msg);
		}
	}

	private void handlePublish(final @NotNull ChannelHandlerContext ctx, final @NotNull Publish publish,
							   final @NotNull String client) throws Exception {
		// QoS 1 or 2 需要处理重传

	}
}
