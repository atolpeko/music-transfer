package com.mf.api.usecase.impl;

import com.mf.api.domain.service.JWTService;
import com.mf.api.domain.service.exception.InvalidJWTException;
import com.mf.api.port.JWTValidatorPort;
import com.mf.api.port.exception.RestException;
import com.mf.api.usecase.AuthUseCase;
import com.mf.api.usecase.exception.AuthorizationException;
import com.mf.api.usecase.exception.UseCaseException;
import com.mf.api.usecase.valueobject.TokenMap;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AuthUseCaseImpl implements AuthUseCase {

	private final JWTValidatorPort jwtValidator;
	private final JWTService jwtService;

	@Override
	public TokenMap extractTokens(String authToken) {
		try {
			var jwt = authToken.replace("Bearer ", "");
			if (!jwtValidator.isValid(jwt)) {
				throw new AuthorizationException("Invalid JWT provided");
			}

			var tokens = jwtService.extractTokens(jwt);
			return TokenMap.from(tokens);
		} catch (AuthorizationException e) {
			throw e;
		} catch (InvalidJWTException e) {
			throw new AuthorizationException("Invalid JWT provided", e);
		} catch (RestException e) {
			var msg = "Failed to access JWT validation service: %s"
				.formatted(e.getMessage());
			throw new UseCaseException(msg, e);
		} catch (Exception e) {
			var msg = "Service unavailable: %s".formatted(e.getMessage());
			throw new UseCaseException(msg, e);
		}
	}
}
