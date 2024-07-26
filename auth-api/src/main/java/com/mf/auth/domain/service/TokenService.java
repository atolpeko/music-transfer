package com.mf.auth.domain.service;

import com.mf.auth.domain.entity.Token;

public interface TokenService {

	/**
	 * Generate new unique token.
	 *
	 * @param expiresSecs  expiration time in seconds
	 *
	 * @return  new unique token
	 */
	Token generate(int expiresSecs);
}
