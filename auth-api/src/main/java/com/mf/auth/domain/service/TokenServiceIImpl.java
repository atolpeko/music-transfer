package com.mf.auth.domain.service;

import com.mf.auth.domain.entity.Token;

import java.util.UUID;

public class TokenServiceIImpl implements TokenService {

	@Override
	public Token generate(int expiresSecs) {
		var value = UUID.randomUUID().toString();
		return new Token(value, expiresSecs);
	}
}
