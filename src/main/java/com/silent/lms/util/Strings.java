package com.silent.lms.util;

import com.google.common.annotations.VisibleForTesting;
import com.silent.lms.annotations.NotNull;
import io.netty.buffer.ByteBuf;

import java.util.Locale;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.nio.charset.StandardCharsets.UTF_8;

public class Strings {

	private Strings() {
		// 不允许实例化
	}

	/**
	 * 根据MQTT规范使用 {@link io.netty.buffer.ByteBuf} 返回一个 ‘带前缀的’ UTF-8 字符串
	 * 这个UTF-8字符串前缀是两个字节无符号值，这个前缀值表示字符串的实际长度
	 * <p>
	 * <b>从 {@link io.netty.buffer.ByteBuf} 读取，并且会改变 ByteBuf 的 reader index</b>
	 *
	 * @param buf {@link io.netty.buffer.ByteBuf}
	 * @return UTF-8 字符串或者在没有足够字节可读的时候返回 <code>null</code>
	 * @throws java.lang.NullPointerException  buf 是 <code>null</code>
	 */
	public static String getPrefixedString(final ByteBuf buf) {
		checkNotNull(buf);
		if (buf.readableBytes() < 2) {
			return null;
		}

		final int utf8StringLength = buf.readUnsignedShort();

		if (buf.readableBytes() < utf8StringLength) {
			return null;
		}

		return getPrefixedString(buf, utf8StringLength);
	}

	public static String getPrefixedString(final ByteBuf buf, final int utf8StringLength) {
		checkNotNull(buf);
		final String string = buf.toString(buf.readerIndex(), utf8StringLength, UTF_8);
		// ByteBuf.toString 不会改变reader index, 因此需要手动改变
		buf.skipBytes(utf8StringLength);
		return string;
	}

	/**
	 * 返回期望长度的UTF-8字符串，并且验证读到的字节数组是否满足UTF-8标准
	 * @param buf
	 * @param utf8StringLength
	 * @param validateShouldNotCharacters
	 * @return
	 */
	public static String getValidatedPrefixedString(@NotNull final ByteBuf buf, final int utf8StringLength,
													final boolean validateShouldNotCharacters) {
		checkNotNull(buf);

		if (buf.readableBytes() < utf8StringLength) {
			return null;
		}

		final byte[] bytes = new byte[utf8StringLength];

		buf.getBytes(buf.readerIndex(), bytes);

		if (Utf8Utils.containsMustNotCharacters(bytes)) {
			return null;
		}

		if (validateShouldNotCharacters && Utf8Utils.hasControlOrNonCharacter(bytes)) {
			return null;
		}
		// ByteBuf.toString 不会改变reader index, 因此需要手动改变
		buf.skipBytes(utf8StringLength);
		return new String(bytes, UTF_8);
	}

	/**
	 * Writes a String onto a {@link io.netty.buffer.ByteBuf}. This encodes the
	 * String according to the MQTT spc. The string gets prefixed with a 16-bit value
	 * which indicates the actual length of the string
	 *
	 * @param string the string to encode
	 * @param buffer the byte buffer
	 * @return the encoded string as {@link io.netty.buffer.ByteBuf}
	 */
	public static ByteBuf createPrefixedBytesFromString(final String string, final ByteBuf buffer) {
		checkNotNull(string);
		checkNotNull(buffer);

		if (Utf8Utils.stringIsOneByteCharsOnly(string)) {
			// In case ther is no character in the string that is encoded with more than one byte in UTF-8,
			// We can write the string character by character without copying it to a temporary byte array.
			buffer.writeShort(string.length());
			for (int i = 0; i < string.length(); i++) {
				buffer.writeByte(string.charAt(i));
			}
		} else {
			final byte[] bytes = string.getBytes(UTF_8);
			buffer.writeShort(bytes.length);
			buffer.writeBytes(bytes);
		}


		return buffer;
	}

	/**
	 * <p>This method can be used to convert a long value into a human readable byte format</p>
	 *
	 * <p>1024 bytes = 1.00 KB</p>
	 * <p>1024*1024 bytes = 1.00 MB</p>
	 * <p>1024*1024*1024 bytes = 1.00 GB</p>
	 * <p>1024*1024*1024*1024 bytes = 1.00 TB</p>
	 *
	 * @param bytes the long value to convert
	 * @return the human readable converted String
	 */
	@VisibleForTesting
	public static String convertBytes(final long bytes) {
		final long kbDivisor = 1024L;
		final long mbDivisor = kbDivisor * kbDivisor;
		final long gbDivisor = mbDivisor * kbDivisor;
		final long tbDivisor = gbDivisor * kbDivisor;

		if (bytes <= kbDivisor) {
			return bytes + " B";
		} else if (bytes <= mbDivisor) {
			final double kb = (double) bytes / kbDivisor;
			return String.format(Locale.US, "%.2f", kb) + " KB";
		} else if (bytes <= gbDivisor) {
			final double mb = (double) bytes / mbDivisor;
			return String.format(Locale.US, "%.2f", mb) + " MB";
		} else if (bytes <= tbDivisor) {
			final double gb = (double) bytes / gbDivisor;
			return String.format(Locale.US, "%.2f", gb) + " GB";
		} else {
			final double tb = (double) bytes / tbDivisor;
			return String.format(Locale.US, "%.2f", tb) + " TB";
		}
	}
}
