package com.silent.lms;

import com.google.inject.Injector;
import com.silent.lms.annotations.NotNull;
import com.silent.lms.bootstrap.LmsNettyBootstrap;
import com.silent.lms.bootstrap.ioc.GuiceBootstrap;
import com.silent.lms.configuration.LmsServerId;
import io.netty.channel.ChannelFuture;
import lombok.extern.log4j.Log4j2;

import javax.inject.Inject;

/**
 * @author gy
 * @version 1.0
 * @date 2021/5/2
 * @description:
 */
@Log4j2
public class LmsServer {
	@NotNull
	private final LmsNettyBootstrap nettyBootstrap;

	@Inject
	public LmsServer(@NotNull LmsNettyBootstrap nettyBootstrap) {
		this.nettyBootstrap = nettyBootstrap;
	}

	public void start() throws Exception {
		ChannelFuture future = nettyBootstrap.bootstrapServer();

	}

	public static void main(String[] args) throws Exception {
		// TODO 指标采集上报
		// TODO 系统信息采集
		// TODO 日志模块
		// TODO 生命周期模块

		LmsServerId lmsServerId = LmsServerId.instance;
		log.info("LMS ID {}", lmsServerId.get());

		log.trace("初始化 Guice");
		final Injector injector = GuiceBootstrap.bootstrapInjector(lmsServerId);

		if (injector == null) {
			return;
		}

		final LmsServer instance = injector.getInstance(LmsServer.class);
		instance.start();
	}
}
