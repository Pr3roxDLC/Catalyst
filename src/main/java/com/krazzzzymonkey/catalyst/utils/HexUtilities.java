/**
 *
 * Copyright 2014-2017 Florian Schmaus
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.krazzzzymonkey.catalyst.utils;

import java.util.Locale;

/**
 * This class contains a few utility methods to encode and decode to HEX from raw bytes.
 */
public final class HexUtilities {

	/**
	 * Converts the given byte array to a String in HEX representation.
	 *
	 * @param bytes the bytes to convert.
	 * @param uppercase whether the resulting HEX string should be in uppercase format.
	 * @param semicolons whether the hex string should be formatted with semicolons ':'.
	 * @return a String in HEX format.
	 */
	public static String encodeToHex(byte[] bytes, boolean uppercase, boolean semicolons) {
		final char[] lookupTable = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
				'e', 'f' };

		final int multiplier = semicolons ? 3 : 2;
		final char[] hexChars = new char[bytes.length * multiplier - (semicolons ? 1 : 0)];
		for (int i = 0; i < bytes.length; i++) {
			final int v = bytes[i] & 0xFF;
			hexChars[i * multiplier] = lookupTable[v >>> 4];
			hexChars[i * multiplier + 1] = lookupTable[v & 0x0F];
			if (i < bytes.length - 1) {
				hexChars[i * multiplier + 2] = ':';
			}
		}
		return uppercase ? new String(hexChars).toUpperCase(Locale.US) : new String(hexChars);
	}

	/**
	 * Converts the given byte array to a String in HEX representation.
	 *
	 * @param bytes the bytes to convert.
	 * @return a String in HEX format.
	 */
	public static String encodeToHex(byte[] bytes) {
		return encodeToHex(bytes, false, false);
	}

	/**
	 * Converts the given string in HEX representation to a byte array, ignores whitespaces and semicolons while doing
	 * so.
	 *
	 * @param hexString the HEX string to convert.
	 * @return a byte array which represents the given HEX string.
	 * @throws IllegalArgumentException if the given HEX string is not valid.
	 */
	public static byte[] decodeFromHex(String hexString) throws IllegalArgumentException {
		// This code is actually a slightly modified copy of the code in the Pin(String) constructor.
		hexString = hexString.toLowerCase(Locale.US);
		// Replace all ':' and whitespace characters with the empty string, i.e. remove them from pinHexString
		hexString = hexString.replaceAll("[:\\s]", "");

		final char[] pinHexChars = hexString.toCharArray();
		final int length = pinHexChars.length;
		if (length % 2 != 0) {
			throw new IllegalArgumentException(
					"HEX String length, with whitespace and semicolons removed, must be divisible by 2 as two characters are needed to decode a single byte!");
		}

		final byte[] pinBytes = new byte[length / 2];
		for (int i = 0; i < length; i += 2) {

			final char one = pinHexChars[i];
			final char two = pinHexChars[i + 1];
			if (!((one >= 'a' && one <= 'f') || (one >= '0' && one <= '9'))
					|| !((two >= 'a' && two <= 'f') || (two >= '0' && two <= '9'))) {
				throw new IllegalArgumentException(
						"HEX String must only contain whitespaces, semicolons (':'), and ASCII letters [a-fA-F] and numbers [0-9]!");
			}

			pinBytes[i / 2] = (byte) ((Character.digit(one, 16) << 4) + Character.digit(two, 16));
		}
		return pinBytes;
	}
}
