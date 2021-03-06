package com.silent.lms.codec.encoder.helper;

import com.silent.lms.annotations.NotNull;
import io.netty.buffer.ByteBuf;

public class MqttVariableByteInteger {

    public static final int NOT_ENOUGH_BYTES = -1;
    public static final int TOO_LARGE = -2;
    public static final int NOT_MINIMUM_BYTES = -3;
    private static final int CONTINUATION_BIT_MASK = 0x80;
    private static final int VALUE_MASK = 0x7f;
    private static final int VALUE_BITS = 7;
    private static final int MAX_SHIFT = VALUE_BITS * 3;
    private static final int ONE_BYTE_MAX_VALUE = (1 << VALUE_BITS) - 1;
    private static final int TWO_BYTES_MAX_VALUE = (1 << (VALUE_BITS * 2)) - 1;
    private static final int THREE_BYTES_MAX_VALUE = (1 << (VALUE_BITS * 3)) - 1;
    public static final int FOUR_BYTES_MAX_VALUE = (1 << (VALUE_BITS * 4)) - 1;
    public static final int MAXIMUM_PACKET_SIZE_LIMIT = 1 + 4 + FOUR_BYTES_MAX_VALUE;

    private MqttVariableByteInteger() {
    }

    /**
     * Decodes a variable byte integer from the given byte buffer at the current reader index.
     * <p>
     * In case of a wrong encoding the reader index of the byte buffer will be in an undefined state after the method
     * returns.
     *
     * @param byteBuf the buffer to decode from.
     * @return the decoded integer value or {@link #NOT_ENOUGH_BYTES} if there are not enough
     * bytes in the byte buffer or {@link #TOO_LARGE} if the encoded variable byte integer has
     * more than 4 bytes or {@link #NOT_MINIMUM_BYTES} if the value is not encoded with a minimum
     * number of bytes.
     */
    public static int decode(@NotNull final ByteBuf byteBuf) {
        byte encodedByte;
        int value = 0;
        byte shift = 0;

        do {
            if (shift > MAX_SHIFT) {
                return TOO_LARGE;
            }
            if (!byteBuf.isReadable()) {
                return NOT_ENOUGH_BYTES;
            }
            encodedByte = byteBuf.readByte();
            final int encodedByteValue = encodedByte & VALUE_MASK;
            value += encodedByteValue << shift;
            shift += VALUE_BITS;
        } while ((encodedByte & CONTINUATION_BIT_MASK) != 0);

        if (shift > VALUE_BITS && encodedByte == 0) {
            return NOT_MINIMUM_BYTES;
        }

        return value;
    }

    /**
     * Encodes the given value as a variable byte integer to the given byte buffer at the current writer index.
     * <p>
     * This method does not check if the value is in range of a 4 byte variable byte integer.
     *
     * @param value   the value to encode.
     * @param byteBuf the byte buffer to encode to.
     */
    public static void encode(int value, @NotNull final ByteBuf byteBuf) {
        do {
            int encodedByte = value & VALUE_MASK;
            value >>>= 7;
            if (value > 0) {
                encodedByte |= CONTINUATION_BIT_MASK;
            }
            byteBuf.writeByte(encodedByte);
        } while (value > 0);
    }

    /**
     * Encodes the given value as a variable byte integer to an int array.
     * <p>
     * This method does not check if the value is in range of a 4 byte variable byte integer.
     *
     * @param value   the value to encode.
     */
    public static int[] encode(int value) {
        final int[] ints = new int[encodedLength(value)];
        int count = 0;
        do {
            int encodedByte = value & VALUE_MASK;
            value >>>= 7;
            if (value > 0) {
                encodedByte |= CONTINUATION_BIT_MASK;
            }
            ints[count++] = encodedByte;
        } while (value > 0);
        return ints;
    }

    /**
     * Checks if the given value is in range of a 4 byte variable byte integer.
     *
     * @param value the value to check.
     * @return whether the value is in range of a 4 byte variable byte integer.
     */
    public static boolean isInRange(final int value) {
        return (value >= 0) && (value <= FOUR_BYTES_MAX_VALUE);
    }

    /**
     * Calculates the byte count of the given value encoded as a variable byte integer.
     * <p>
     * This method does not check if the value is in range of a 4 byte variable byte integer.
     *
     * @param value the value to calculate the encoded length for.
     * @return the encoded length of the value.
     */
    public static int encodedLength(final int value) {
        int length = 1;
        if (value > ONE_BYTE_MAX_VALUE) {
            length++;
            if (value > TWO_BYTES_MAX_VALUE) {
                length++;
                if (value > THREE_BYTES_MAX_VALUE) {
                    length++;
                }
            }
        }
        return length;
    }

}