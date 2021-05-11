package com.silent.lms.bootstrap;

import com.google.common.net.InetAddresses;
import com.silent.lms.annotations.NotNull;
import com.silent.lms.bootstrap.netty.ChannelInitializerFactory;
import com.silent.lms.bootstrap.netty.ioc.NettyConfigurator;
import com.silent.lms.configuration.Listener;
import com.silent.lms.configuration.TcpListener;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.WriteBufferWaterMark;
import lombok.extern.log4j.Log4j2;

import javax.inject.Inject;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

/**
 * @author gy
 * @version 1.0
 * @date 2021/5/6.
 * @description:
 */
@Log4j2
public class LmsNettyBootstrap {
	private final @NotNull ChannelInitializerFactory channelInitializerFactory;
	private final @NotNull NettyConfigurator nettyConfigurator;

	@Inject
	public LmsNettyBootstrap(@NotNull ChannelInitializerFactory channelInitializerFactory, @NotNull NettyConfigurator nettyConfigurator) {
		this.channelInitializerFactory = channelInitializerFactory;
		this.nettyConfigurator = nettyConfigurator;
	}

	public ChannelFuture bootstrapServer() {
		TcpListener tcpListener = new TcpListener(1808, "0.0.0.0");
		return buildServer(tcpListener);
	}

	private ChannelFuture buildServer(final @NotNull TcpListener tcpListener) {
		log.trace("TCP listener");
		final ServerBootstrap b = createServerBootstrap(nettyConfigurator.getParentEventLoopGroup(),
				nettyConfigurator.getChildEventLoopGroup(), tcpListener);
		final String bindAddress = tcpListener.getBindAddress();

		final Integer port = tcpListener.getPort();

		log.info("Starting TCP listener on address {} and port {}", bindAddress, port);
		final ChannelFuture bind = b.bind(createInetSocketAddress(bindAddress, port));
		return bind;
	}

	@NotNull
	private ServerBootstrap createServerBootstrap(final @NotNull EventLoopGroup bossGroup, final @NotNull EventLoopGroup workerGroup,
												  final @NotNull Listener listener) {
		final ServerBootstrap b = new ServerBootstrap();
		b.group(bossGroup, workerGroup)
				.channel(nettyConfigurator.getServerSocketChannelClass())
				.childHandler(channelInitializerFactory.getChannelInitializer(listener))
				.option(ChannelOption.SO_BACKLOG, 128)
				.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
				.childOption(ChannelOption.SO_KEEPALIVE, true)
				.childOption(ChannelOption.TCP_NODELAY, true)
				.childOption(ChannelOption.SO_REUSEADDR, true)
				.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

		setAdvancedOptions(b);
		return b;
	}

	private void setAdvancedOptions(final @NotNull ServerBootstrap b) {

		final int sendBufferSize = -1;
		final int receiveBufferSize = -1;

		if (sendBufferSize > -1) {
			b.childOption(ChannelOption.SO_SNDBUF, sendBufferSize);
		}
		if (receiveBufferSize > -1) {
			b.childOption(ChannelOption.SO_RCVBUF, receiveBufferSize);
		}

		// 64k
		final int writeBufferHigh = 65536;
		// 32k
		final int writeBufferLow = 32768;

		b.childOption(ChannelOption.WRITE_BUFFER_WATER_MARK, new WriteBufferWaterMark(writeBufferLow, writeBufferHigh));
	}

	@NotNull
	private InetSocketAddress createInetSocketAddress(final @NotNull String ip, final int port) {
		InetAddress inetAddress;
		try {
			inetAddress = InetAddresses.forString(ip);
		} catch (final IllegalArgumentException e) {
			log.debug("通过DNS查找IP", ip);
			try {
				inetAddress = InetAddress.getByName(ip);
			} catch (final UnknownHostException e1) {
				log.error("无法绑定 hostname {}", ip);
				throw new RuntimeException("");
			}
		}

		return new InetSocketAddress(inetAddress, port);
	}
}
