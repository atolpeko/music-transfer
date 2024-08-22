package com.mf.api.usecase.valueobject;

import com.mf.api.domain.entity.OAuth2Token;
import java.util.Map;
import lombok.AllArgsConstructor;

/**
 * Mapping of service names to OAuth2 tokens.
 */
@AllArgsConstructor
public class TokenMap {

	private final Map<String, OAuth2Token> serviceToTokens;

	public static TokenMap from(Map<String, OAuth2Token> serviceToTokens) {
		return new TokenMap(serviceToTokens);
	}

	public OAuth2Token get(String name) {
		return serviceToTokens.get(name);
	}
}
