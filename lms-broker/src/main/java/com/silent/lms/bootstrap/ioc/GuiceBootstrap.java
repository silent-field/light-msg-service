package com.silent.lms.bootstrap.ioc;

import com.google.common.collect.ImmutableList;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import com.silent.lms.annotations.NotNull;
import com.silent.lms.annotations.Nullable;
import com.silent.lms.bootstrap.netty.ioc.NettyModule;
import com.silent.lms.configuration.LmsServerId;
import com.silent.lms.mqtt.ioc.MQTTHandlerModule;
import lombok.extern.log4j.Log4j2;

/**
 * @author gy
 * @version 1.0
 * @date 2021/5/6.
 * @description:
 */
@Log4j2
public class GuiceBootstrap {
	@Nullable
	public static Injector bootstrapInjector(
			final @NotNull LmsServerId lmsServerId) {
		final ImmutableList.Builder<AbstractModule> modules = ImmutableList.builder();

		modules.add(
				/* Binds netty specific classes */
				new NettyModule(),
				/* Binds MQTT handler specific classes */
				new MQTTHandlerModule()
				);

		try {
			return Guice.createInjector(Stage.PRODUCTION, modules.build());
		} catch (final Exception e) {
			log.error("初始化Guice", e);
			return null;
		}
	}
}
