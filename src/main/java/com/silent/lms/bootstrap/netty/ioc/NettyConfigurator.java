package com.silent.lms.bootstrap.netty.ioc;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.silent.lms.annotations.NotNull;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.Getter;

import javax.inject.Singleton;
import java.util.concurrent.ThreadFactory;

import static com.google.common.base.Preconditions.checkNotNull;


@Singleton
public class NettyConfigurator {
	@Getter
	private final Class<? extends ServerSocketChannel> serverSocketChannelClass;

	@Getter
	private final Class<? extends SocketChannel> clientSocketChannelClass;

	@Getter
	private final EventLoopGroup parentEventLoopGroup;

	@Getter
	private final EventLoopGroup childEventLoopGroup;

	public NettyConfigurator() {
		this.serverSocketChannelClass = NioServerSocketChannel.class;
		this.clientSocketChannelClass = NioSocketChannel.class;
		this.parentEventLoopGroup = createParentEventLoop();
		this.childEventLoopGroup = createChildEventLoop();
	}

	@NotNull
	private EventLoopGroup createParentEventLoop() {
		return new NioEventLoopGroup(1, createThreadFactory("lms-eventLoop-parent-%d"));
	}

	@NotNull
	private EventLoopGroup createChildEventLoop() {
		return new NioEventLoopGroup(0, createThreadFactory("lms-eventLoop-child-%d"));
	}

	private ThreadFactory createThreadFactory(final @NotNull String nameFormat) {
		checkNotNull(nameFormat, "ThreadFactory nameFormat 不能为空");
		return new ThreadFactoryBuilder().setNameFormat(nameFormat).build();
	}
}
