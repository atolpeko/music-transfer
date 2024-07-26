package com.mf.auth.domain.service;

import com.mf.auth.domain.entity.JWT;
import com.mf.auth.domain.entity.OAuth2Token;
import com.mf.auth.domain.entity.Token;
import com.mf.auth.domain.exception.InvalidJWTException;

import java.util.Map;

public interface JWTService {

	/**
	 * Generate a JWT.
	 *
	 * @param uuid             UUID
	 * @param serviceToTokens  a map of service names to OAuth2 tokens
	 *
	 * @return generated JWT
	 */
	JWT generate(Token uuid, Map<String, OAuth2Token> serviceToTokens);

	/**
	 * Update the specified JWT.
	 *
	 * @param uuid             UUID
	 * @param jwt              JWT to update
	 * @param serviceToTokens  a map of service names to OAuth2 tokens
	 *
	 * @return updated JWT
	 *
	 * @throws InvalidJWTException if provided JWT is invalid
	 */
	JWT update(Token uuid, JWT jwt, Map<String, OAuth2Token> serviceToTokens);

	/**
	 * Validates if the specified JWT is valid.
	 *
	 * @param jwt  JWT to validate
	 *
	 * @return true if this JWT is valid, false otherwise
	 */
	boolean isValid(JWT jwt);
}
