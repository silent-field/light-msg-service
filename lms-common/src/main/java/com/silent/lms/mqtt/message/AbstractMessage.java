package com.silent.lms.mqtt.message;

/**
 * @author gy
 * @version 1.0
 * @date 2021/5/2
 * @description:
 */
public abstract class AbstractMessage implements Message {
	public AbstractMessage() {
	}

	@Override
	public void setEncodedLength(final int bufferSize) {
	}

	@Override
	public int getEncodedLength() {
		return 0;
	}

	@Override
	public int getRemainingLength() {
		return 0;
	}

	@Override
	public void setRemainingLength(final int remainingLength) {

	}

	@Override
	public int getPropertyLength() {
		return 0;
	}

	@Override
	public void setPropertyLength(final int propertyLength) {

	}

	@Override
	public int getOmittedProperties() {
		return 0;
	}

	@Override
	public void setOmittedProperties(final int omittedProperties) {
	}
}
