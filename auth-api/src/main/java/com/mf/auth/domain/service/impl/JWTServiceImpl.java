package com.mf.auth.domain.service.impl;

import com.mf.auth.domain.service.properties.ServiceProperties;
import com.mf.auth.domain.entity.JWT;
import com.mf.auth.domain.entity.OAuth2Token;
import com.mf.auth.domain.entity.Token;
import com.mf.auth.domain.service.exception.InvalidJWTException;
import com.mf.auth.domain.service.impl.mapper.OAuth2TokenMapper;
import com.mf.auth.domain.service.JWTService;

import io.jsonwebtoken.Claims;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import java.util.Map.Entry;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JWTServiceImpl implements JWTService {

	private final JWTGenerator jwtGenerator;
	private final JWTDecryptor jwtDecryptor;
	private final OAuth2TokenMapper mapper;
	private final ServiceProperties properties;

	@Override
	public JWT generate(Token uuid, Map<String, OAuth2Token> serviceToTokens) {
		var token = jwtGenerator.generate(
			properties.jwtSecret(),
			properties.jwtIssuer(),
			properties.jwtExpirationSeconds(),
			uuid.getValue(),
			serviceToTokens
		);

		return new JWT(token, properties.jwtExpirationSeconds());
	}

	@Override
	public JWT update(Token uuid, JWT jwt, Map<String, OAuth2Token> serviceToTokens) {
		var claims = jwtDecryptor.decrypt(properties.jwtSecret(), jwt.getValue());
		if (!isValid(claims)) {
			throw new InvalidJWTException("Provided JWT is invalid");
		}

		var tokens = extractTokens(claims);
		tokens.putAll(serviceToTokens);

		return generate(uuid, tokens);
	}

	@Override
	public boolean isValid(String jwt) {
		try {
			var claims = jwtDecryptor.decrypt(properties.jwtSecret(), jwt);
			return isValid(claims) && allTokensValid(claims);
		} catch (Exception e) {
			return false;
		}
	}

	private boolean isValid(Claims claims) {
		return claims.getIssuer().equals(properties.jwtIssuer())
			&& claims.getExpiration().after(new Date());
	}

	private boolean allTokensValid(Claims claims) {
		return extractTokens(claims).values().stream()
			.allMatch(OAuth2Token::isValid);
	}

	private Map<String, OAuth2Token> extractTokens(Claims claims) {
		return claims.entrySet().stream()
			.filter(record -> record.getKey().startsWith(JWTGenerator.TOKEN_PREFIX))
			.map(record -> Map.entry(
				record.getKey().replace(JWTGenerator.TOKEN_PREFIX, ""),
				mapper.map((LinkedHashMap<String, Object>) record.getValue()))
			)
			.collect(Collectors.toMap(Entry::getKey, Entry::getValue));
	}
}
