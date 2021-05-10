package com.silent.lms.bootstrap.netty;

import com.silent.lms.annotations.NotNull;
import com.silent.lms.bootstrap.netty.initializer.AbstractChannelInitializer;
import com.silent.lms.bootstrap.netty.initializer.TcpChannelInitializer;
import com.silent.lms.configuration.Listener;
import com.silent.lms.configuration.TcpListener;
import com.silent.lms.logging.EventLog;

import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author gy
 * @version 1.0
 * @date 2021/5/3
 * @description:
 */
public class ChannelInitializerFactoryImpl implements ChannelInitializerFactory {
	@NotNull
	private final ChannelDependencies channelDependencies;

	@NotNull
	private final EventLog eventLog;

	@Inject
	public ChannelInitializerFactoryImpl(@NotNull final ChannelDependencies channelDependencies,
										 @NotNull final EventLog eventLog) {
		this.channelDependencies = channelDependencies;
		this.eventLog = eventLog;
	}

	@Override
	public AbstractChannelInitializer getChannelInitializer(Listener listener) {
		checkNotNull(listener, "Listener must not be null");

		if (listener instanceof TcpListener) {
			return createTcpInitializer((TcpListener) listener);
		}

		throw new IllegalArgumentException("Unknown listener type");
	}

	@NotNull
	protected AbstractChannelInitializer createTcpInitializer(@NotNull final TcpListener listener) {
		return new TcpChannelInitializer(channelDependencies, listener);
	}
}
