package com.silent.lms.codec.encoder.helper;

import com.silent.lms.annotations.NotNull;
import com.silent.lms.annotations.Nullable;
import com.silent.lms.codec.encoder.helper.MqttBinaryData;
import com.silent.lms.codec.encoder.helper.MqttVariableByteInteger;
import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;

public class MqttMessageEncoderUtil {

	private MqttMessageEncoderUtil() {
	}

	/**
	 * Calculates the encoded length of a MQTT message with the given remaining length.
	 *
	 * @param remainingLength the remaining length of the MQTT message.
	 * @return the encoded length of the MQTT message.
	 */
	public static int encodedPacketLength(final int remainingLength) {
		return 1 + encodedLengthWithHeader(remainingLength);
	}

	/**
	 * Calculates the encoded length with a prefixed header.
	 *
	 * @param encodedLength the encoded length.
	 * @return the encoded length with a prefixed header.
	 */
	public static int encodedLengthWithHeader(final int encodedLength) {
		return MqttVariableByteInteger.encodedLength(encodedLength) + encodedLength;
	}

	public static int nullableEncodedLength(@Nullable final String string) {
		return (string == null) ? 0 : MqttBinaryData.encodedLength(string);
	}

	public static int nullableEncodedLength(@Nullable final ByteBuffer byteBuffer) {
		return (byteBuffer == null) ? 0 : MqttBinaryData.encodedLength(byteBuffer);
	}

	public static int encodedOrEmptyLength(@Nullable final ByteBuffer byteBuffer) {
		return (byteBuffer == null) ? MqttBinaryData.EMPTY_LENGTH : MqttBinaryData.encodedLength(byteBuffer);
	}

	public static void encodeNullable(@Nullable final String string, @NotNull final ByteBuf out) {
		if (string != null) {
			MqttBinaryData.encode(string, out);
		}
	}

	public static void encodeNullable(@Nullable final ByteBuffer byteBuffer, @NotNull final ByteBuf out) {
		if (byteBuffer != null) {
			MqttBinaryData.encode(byteBuffer, out);
		}
	}

	public static void encodeOrEmpty(@Nullable final ByteBuffer byteBuffer, @NotNull final ByteBuf out) {
		if (byteBuffer != null) {
			MqttBinaryData.encode(byteBuffer, out);
		} else {
			MqttBinaryData.encodeEmpty(out);
		}
	}

}