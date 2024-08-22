package com.mf.api.usecase;

import com.mf.api.usecase.exception.AuthorizationException;
import com.mf.api.usecase.valueobject.TokenMap;

public interface AuthUseCase {

	/**
	 * Extract OAuth2 tokens from auth token claims.
	 *
	 * @param authToken  token
	 *
	 * @return a map of music services to tokens
	 *
	 * @throws AuthorizationException  if provided token is invalid
	 */
	TokenMap extractTokens(String authToken);
}
