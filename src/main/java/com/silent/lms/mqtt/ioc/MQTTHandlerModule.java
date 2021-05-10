package com.silent.lms.mqtt.ioc;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.silent.lms.ioc.SingletonModule;
import com.silent.lms.mqtt.handler.connack.MqttConnacker;
import com.silent.lms.mqtt.handler.connack.MqttConnackerImpl;
import com.silent.lms.mqtt.handler.disconnect.MqttServerDisconnector;
import com.silent.lms.mqtt.handler.disconnect.MqttServerDisconnectorImpl;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;

import javax.inject.Singleton;

/**
 * @author gy
 * @version 1.0
 * @date 2021/5/6.
 * @description:
 */
public class MQTTHandlerModule extends SingletonModule<Class<MQTTHandlerModule>> {
	public MQTTHandlerModule() {
		super(MQTTHandlerModule.class);
	}

	@Override
	protected void configure() {
		int nThread = Runtime.getRuntime().availableProcessors() * 2;
		final DefaultEventExecutorGroup mqttHandlerWorker = new DefaultEventExecutorGroup(nThread, new ThreadFactoryBuilder().
				setNameFormat("lms-event-executor-%d").build());

		bind(EventExecutorGroup.class).toInstance(mqttHandlerWorker);

		bind(MqttServerDisconnector.class).to(MqttServerDisconnectorImpl.class).in(Singleton.class);
		bind(MqttConnacker.class).to(MqttConnackerImpl.class).in(Singleton.class);
	}
}
