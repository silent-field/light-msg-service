package com.silent.lms.mqtt.message.connect;

import com.google.common.base.Preconditions;
import com.silent.lms.annotations.NotNull;
import com.silent.lms.annotations.Nullable;
import com.silent.lms.mqtt.message.QoS;

/**
 * @author gy
 * @version 1.0
 * @date 2021/5/2
 * @description: 遗嘱信息
 */
public class MqttWill {
    public static final long WILL_DELAY_INTERVAL_NOT_SET = Long.MAX_VALUE;
    public static final long WILL_DELAY_INTERVAL_DEFAULT = 0;

    //MQTT 5 and 3
    private final String topic;
    private byte[] payload;
    private final QoS qos;
    private final boolean retain;

    protected MqttWill(
            @NotNull final String topic,
            @Nullable final byte[] payload,
            @NotNull final QoS qos,
            final boolean retain) {

        Preconditions.checkNotNull(topic, "topic不可以为空");
        Preconditions.checkNotNull(qos, "QoS不可以为空");

        this.topic = topic;
        this.payload = payload;
        this.qos = qos;
        this.retain = retain;
    }

    public static class Builder {
        private String topic;
        private byte[] payload;
        private QoS qos;
        private boolean retain;

        public MqttWill build() {
            return new MqttWill(topic, payload, qos, retain);
        }

        public Builder withTopic(final String topic) {
            this.topic = topic;
            return this;
        }

        public Builder withPayload(final byte[] payload) {
            this.payload = payload;
            return this;
        }

        public Builder withQos(final QoS qos) {
            this.qos = qos;
            return this;
        }

        public Builder withRetain(final boolean retain) {
            this.retain = retain;
            return this;
        }
    }
}
