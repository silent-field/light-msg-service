package com.silent.lms.mqtt.message;

import com.silent.lms.configuration.InternalConfigurations;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author gy
 * @version 1.0
 * @date 2021/5/2
 * @description:
 */
public abstract class AbstractMessageWithID implements Message{
    @Getter
    protected int packetId;

    protected int bufferSize = -1;
    protected int remainingLength = -1;
    protected int propertyLength = -1;
    protected int omittedProperties = -1;

    public AbstractMessageWithID() {
    }

    public AbstractMessageWithID(final int packetId) {
        this.packetId = packetId;
    }

    public void setPacketId(final int packetId) {
        // 只有在publish，且QoS = 0的控制消息中，才会出现-1
        if (!InternalConfigurations.PACKET_ID_RANGE.contains(packetId)) {
            throw new IllegalArgumentException("PacketID must be between -1 to 65535");
        }
        this.packetId = packetId;
    }

    @Override
    public void setEncodedLength(final int bufferSize) {
        this.bufferSize = bufferSize;
    }

    @Override
    public int getEncodedLength() {
        return bufferSize;
    }

    @Override
    public int getRemainingLength() {
        return remainingLength;
    }

    @Override
    public void setRemainingLength(final int remainingLength) {
        this.remainingLength = remainingLength;
    }

    @Override
    public int getPropertyLength() {
        return propertyLength;
    }

    @Override
    public void setPropertyLength(final int propertyLength) {
        this.propertyLength = propertyLength;
    }

    @Override
    public int getOmittedProperties() {
        return omittedProperties;
    }

    @Override
    public void setOmittedProperties(final int omittedProperties) {
        this.omittedProperties = omittedProperties;
    }
}
