package com.silent.lms.codec.decoder;

import com.silent.lms.annotations.NotNull;
import com.silent.lms.annotations.Nullable;
import com.silent.lms.mqtt.message.Message;
import com.silent.lms.util.ChannelUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;

/**
 * @author gy
 * @version 1.0
 * @date 2021/5/3
 * @description:
 */
@Log4j2
public abstract class MqttDecoder <T extends Message>{
    public abstract @Nullable T decode(final @NotNull Channel channel, final @NotNull ByteBuf buf, final byte header);

    /**
     * 某些协议固定报头低4位必须是0
     * @param header
     * @return
     */
    protected boolean validateHeader(final byte header) {
        return (header & 0b0000_1111) == 0;
    }

    protected boolean isInvalidTopic(final @NotNull Channel channel, final @Nullable String topic) {
        if (StringUtils.isBlank(topic)) {
            if (log.isDebugEnabled()) {
                log.debug("client (IP: {}) 发送了一个空topic，需要关闭这个client.",
                        ChannelUtils.getChannelIP(channel).or("UNKNOWN"));
            }
            return true;
        }

        if (topic.contains("\u0000")) {
            if (log.isDebugEnabled()) {
                log.debug("client (IP: {}) 发送了一个包含Unicode编码的空格的topic(U+0000),需要关闭这个client.",
                        ChannelUtils.getChannelIP(channel).or("UNKNOWN"));
            }
            return true;
        }
        return false;
    }
}
