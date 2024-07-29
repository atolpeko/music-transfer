package com.mf.auth.domain.service;

import com.mf.auth.domain.ServiceProperties;
import com.mf.auth.domain.entity.AccessToken;
import com.mf.auth.domain.entity.Token;

import java.util.UUID;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TokenServiceIImpl implements TokenService {
	private final ServiceProperties properties;

	@Override
	public Token generateUuid() {
		var value = UUID.randomUUID().toString();
		return new Token(value, properties.uuidExpirationSeconds());
	}

	@Override
	public AccessToken generateAccessToken() {
		var value = UUID.randomUUID().toString();
		return new AccessToken(value, properties.accessTokenExpirationSeconds());
	}
}
