package com.silent.lms.util;

import com.google.common.base.Optional;
import io.netty.channel.Channel;
import lombok.extern.log4j.Log4j2;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * @author gy
 * @version 1.0
 * @date 2021/5/3
 * @description:
 */
@Log4j2
public class ChannelUtils {
    private ChannelUtils() {
    }

    public static Optional<String> getChannelIP(final Channel channel) {

        final Optional<InetAddress> inetAddress = getChannelAddress(channel);

        if (inetAddress.isPresent()) {
            return Optional.fromNullable(inetAddress.get().getHostAddress());
        }

        return Optional.absent();
    }

    public static Optional<InetAddress> getChannelAddress(final Channel channel) {
        final Optional<SocketAddress> socketAddress = Optional.fromNullable(channel.remoteAddress());
        if (socketAddress.isPresent()) {
            final SocketAddress sockAddress = socketAddress.get();
            //If this is not an InetAddress, we're treating this as if there's no address
            if (sockAddress instanceof InetSocketAddress) {
                return Optional.fromNullable(((InetSocketAddress) sockAddress).getAddress());
            }
        }
        return Optional.absent();
    }
}
