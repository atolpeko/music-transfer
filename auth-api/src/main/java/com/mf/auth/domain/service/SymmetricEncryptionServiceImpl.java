package com.mf.auth.domain.service;

import com.mf.auth.domain.ServiceProperties;

import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.Cipher;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SymmetricEncryptionServiceImpl implements SymmetricEncryptionService {
	private final ServiceProperties properties;

	@Override
	public String encrypt(String value, String key) {
		try {
			var secretKey = getKeyFromPassword(key);
			var cipher = Cipher.getInstance(properties.encryptionTransformation());
			var iv = new byte[cipher.getBlockSize()];
			var secureRandom = new SecureRandom();
			secureRandom.nextBytes(iv);
			var ivParams = new IvParameterSpec(iv);

			cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParams);
			var encrypted = cipher.doFinal(value.getBytes());

			var withIv = new byte[iv.length + encrypted.length];
			System.arraycopy(iv, 0, withIv, 0, iv.length);
			System.arraycopy(encrypted, 0, withIv, iv.length, encrypted.length);

			return Base64.getEncoder().encodeToString(withIv);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private SecretKeySpec getKeyFromPassword(String password) {
		byte[] key = new byte[properties.encryptionKeySize()];
		byte[] passButes = password.getBytes();
		System.arraycopy(passButes, 0, key, 0, Math.min(passButes.length, key.length));
		return new SecretKeySpec(key, properties.encryptionAlgName());
	}

	@Override
	public String decrypt(String value, String key) {
		try {
			var withIv = Base64.getDecoder().decode(value);
			var iv = new byte[16];
			var encrypted = new byte[withIv.length - iv.length];

			System.arraycopy(withIv, 0, iv, 0, iv.length);
			System.arraycopy(withIv, iv.length, encrypted, 0, encrypted.length);

			var secretKey = getKeyFromPassword(key);
			var cipher = Cipher.getInstance(properties.encryptionTransformation());
			var ivParams = new IvParameterSpec(iv);

			cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParams);
			var decryptedData = cipher.doFinal(encrypted);

			return new String(decryptedData);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
