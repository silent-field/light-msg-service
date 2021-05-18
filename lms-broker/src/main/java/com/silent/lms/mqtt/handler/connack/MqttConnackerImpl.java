package com.silent.lms.mqtt.handler.connack;

import com.silent.lms.annotations.NotNull;
import com.silent.lms.annotations.Nullable;
import com.silent.lms.logging.EventLog;
import com.silent.lms.mqtt.message.ProtocolVersion;
import com.silent.lms.mqtt.message.connack.Connack;
import com.silent.lms.bootstrap.netty.ChannelAttributes;
import com.silent.lms.mqtt.reason.MqttConnAckReasonCode;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.log4j.Log4j2;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.silent.lms.util.ChannelUtils.getChannelIP;

/**
 * @author gy
 * @version 1.0
 * @date 2021/5/3
 * @description:
 */
@Log4j2
@Singleton
public class MqttConnackerImpl implements MqttConnacker {
    private final @NotNull EventLog eventLog;

    @Inject
    public MqttConnackerImpl(final @NotNull EventLog eventLog) {
        this.eventLog = eventLog;
    }

    @Override
    public ChannelFuture connackSuccess(ChannelHandlerContext ctx, Connack connack) {
        final ChannelFuture channelFuture = ctx.writeAndFlush(connack);

        //for preventing success, when a connack will be prevented by an extension
        channelFuture.addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                future.channel().attr(ChannelAttributes.CONNACK_SENT).set(true);
                eventLog.clientConnected(future.channel());
            }
        });

        return channelFuture;
    }

    @Override
    public void connackError(Channel channel, String logMessage, String eventLogMessage, MqttConnAckReasonCode returnCode) {
        final ProtocolVersion protocolVersion = channel.attr(ChannelAttributes.MQTT_VERSION).get();
        logConnack(channel, logMessage, eventLogMessage);
        if (protocolVersion == null) {
            channel.close();
        } else {
            if(null == returnCode){
                channel.close();
            } else {
                channel.writeAndFlush(new Connack(returnCode)).addListener(ChannelFutureListener.CLOSE);
            }
        }
    }

    private void logConnack(
            final @NotNull Channel channel,
            final @Nullable String logMessage,
            final @Nullable String eventLogMessage) {

        if (log.isDebugEnabled() && logMessage != null && !logMessage.isEmpty()) {
            log.debug(logMessage, getChannelIP(channel).or("UNKNOWN"));
        }

        if (eventLogMessage != null && !eventLogMessage.isEmpty()) {
            eventLog.clientWasDisconnected(channel, eventLogMessage);
        }
    }
}
