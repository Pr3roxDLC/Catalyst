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

import java.io.ByteArrayInputStream;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

/**
 * Some utilises for creating X509 certificate related objects, like {@link X509Certificate} or and
 * X509 {@link PublicKey}.
 */
public final class X509CertificateUtilities {

	public static X509Certificate decodeX509Certificate(String hexString) {
		return decodeX509Certificate(HexUtilities.decodeFromHex(hexString));
	}

	public static X509Certificate decodeX509Certificate(byte[] encodedCertificate) {
		try {
			final CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
			return (X509Certificate) certFactory.generateCertificate(new ByteArrayInputStream(encodedCertificate));
		} catch (CertificateException e) {
			throw new IllegalArgumentException("Byte array cannot be decoded", e);
		}
	}

	public static PublicKey decodeX509PublicKey(String hexString) {
		return decodeX509PublicKey(HexUtilities.decodeFromHex(hexString));
	}

	public static PublicKey decodeX509PublicKey(byte[] encodedPublicKeyBytes) {
		try {
			final KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			return keyFactory.generatePublic(new X509EncodedKeySpec(encodedPublicKeyBytes));
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			throw new IllegalArgumentException("Byte array cannot be decoded", e);
		}
	}
}
