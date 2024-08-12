package com.mf.api.domain.service;

import com.mf.api.domain.entity.OAuth2Token;
import com.mf.api.domain.service.exception.InvalidJWTException;

import java.util.Map;

public interface JWTService {

	/**
	 * Extract OAuth2 tokens from JWT claims.
	 *
	 * @param jwt  JWT
	 *
	 * @return a map of music services to tokens
	 *
	 * @throws InvalidJWTException  if provided JWT is invalid
	 */
	Map<String, OAuth2Token> extractTokens(String jwt);
}
