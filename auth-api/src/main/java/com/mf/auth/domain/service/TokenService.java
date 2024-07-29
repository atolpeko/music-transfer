package com.mf.auth.domain.service;

import com.mf.auth.domain.entity.AccessToken;
import com.mf.auth.domain.entity.Token;

public interface TokenService {

	/**
	 * Generate new unique token.
	 *
	 * @return  new unique token
	 */
	Token generateUuid();


	/**
	 * Generate new unique token.
	 *
	 * @return  new unique token
	 */
	AccessToken generateAccessToken();
}
