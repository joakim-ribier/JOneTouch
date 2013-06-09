package fr.rjoakim.android.jonetouch.util;

import java.security.SecureRandom;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import com.google.common.base.Preconditions;

import android.util.Base64;

/**
 * 
 * Copyright 2013 Joakim Ribier
 * 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * 
 * 	http://www.apache.org/licenses/LICENSE-2.0
 * 
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
public class CryptographyUtils {

	private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
	private static final int PKCS5_SALT_LENGTH = 8;

	private static final String PBKDF2_DERIVATION_ALGORITHM = "PBKDF2WithHmacSHA1";

	private static int KEY_LENGTH = 256;
	private static int ITERATION_COUNT = 1000;
	private static String DELIMITER = "]";

	public static String encrypt(String password, String key) throws CryptographyException {
		byte[] salt = generateSalt();
		SecretKey secretKey = deriveKey(salt, key);
		return encrypt(password, secretKey, salt);
	}

	public static String decrypt(String ciphertext, String key) throws CryptographyException {
		String[] fields = ciphertext.split(DELIMITER);
		Preconditions.checkArgument(fields.length == 3, "Invalid encypted text format");

		byte[] salt = fromBase64(fields[0]);
		byte[] iv = fromBase64(fields[1]);
		byte[] cipherBytes = fromBase64(fields[2]);
		SecretKey secretKey = deriveKey(salt, key);
		return decrypt(cipherBytes, secretKey, iv);
	}
	
	private static String decrypt(byte[] cipherBytes, SecretKey key, byte[] iv) throws CryptographyException {
		try {
			Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
			IvParameterSpec ivParams = new IvParameterSpec(iv);
			cipher.init(Cipher.DECRYPT_MODE, key, ivParams);
			byte[] plaintext = cipher.doFinal(cipherBytes);
			return new String(plaintext, "UTF-8");
		} catch (Exception e) {
			throw new CryptographyException(e.getMessage(), e);
		}
	}

	private static String encrypt(String plaintext, SecretKey key, byte[] salt) throws CryptographyException {
		try {
			Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);

			byte[] iv = generateIv(cipher.getBlockSize());
			IvParameterSpec ivParams = new IvParameterSpec(iv);
			cipher.init(Cipher.ENCRYPT_MODE, key, ivParams);
			byte[] cipherText = cipher.doFinal(plaintext.getBytes("UTF-8"));

			if (salt != null) {
				return String.format("%s%s%s%s%s", toBase64(salt), DELIMITER,
						toBase64(iv), DELIMITER, toBase64(cipherText));
			}

			return String.format("%s%s%s", toBase64(iv), DELIMITER, toBase64(cipherText));
		} catch (Exception e) {
			throw new CryptographyException(e.getMessage(), e);
		}
	}

	private static SecretKey deriveKey(byte[] salt, String password) throws CryptographyException {
		try {
			KeySpec keySpec = new PBEKeySpec(password.toCharArray(),
					salt, ITERATION_COUNT, KEY_LENGTH);
			
			SecretKeyFactory keyFactory =
					SecretKeyFactory.getInstance(PBKDF2_DERIVATION_ALGORITHM);
			
			byte[] keyBytes = keyFactory.generateSecret(keySpec).getEncoded();
			return new SecretKeySpec(keyBytes, "AES");
		} catch (Exception e) {
			throw new CryptographyException(e.getMessage(), e);
		}
	}
	
	private static byte[] generateIv(int length) {
		byte[] b = new byte[length];
		new SecureRandom().nextBytes(b);
		return b;
	}

	private static byte[] generateSalt() {
		byte[] b = new byte[PKCS5_SALT_LENGTH];
		new SecureRandom().nextBytes(b);
		return b;
	}
	
	private static String toBase64(byte[] bytes) {
		return Base64.encodeToString(bytes, Base64.NO_WRAP);
	}

	private static byte[] fromBase64(String base64) {
		return Base64.decode(base64, Base64.NO_WRAP);
	}
}
