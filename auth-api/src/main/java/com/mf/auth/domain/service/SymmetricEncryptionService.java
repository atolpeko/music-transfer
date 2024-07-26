package com.mf.auth.domain.service;

/**
 * Used to encrypt / decrypt values using a symmetric algorithm.
 */
public interface SymmetricEncryptionService {

	/**
	 * Encrypt a string using a secret key.
	 *
	 * @param value  value to encrypt
	 * @param key    secret key
	 *
	 * @return encrypted string
	 */
	String encrypt(String value, String key);

	/**
	 * Decrypt a string using a secret key.
	 *
	 * @param value  value to decrypt
	 * @param key    secret key
	 *
	 * @return decrypted string
	 */
	String decrypt(String value, String key);
}
