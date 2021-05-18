package com.silent.lms.codec;

/**
 * @author gy
 * @version 1.0
 * @date 2021/5/8.
 * @description:
 */
public class MqttProtocolConstants {
	private static final int VALUE_BITS = 7;
	private static final int ONE_BYTE_MAX_VALUE = (1 << VALUE_BITS) - 1;
	private static final int TWO_BYTES_MAX_VALUE = (1 << (VALUE_BITS * 2)) - 1;
	private static final int THREE_BYTES_MAX_VALUE = (1 << (VALUE_BITS * 3)) - 1;
	public static final int FOUR_BYTES_MAX_VALUE = (1 << (VALUE_BITS * 4)) - 1;
	public static final int MAXIMUM_PACKET_SIZE_LIMIT = 1 + 4 + FOUR_BYTES_MAX_VALUE;

	private MqttProtocolConstants() {
	}
}
