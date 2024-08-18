package com.mf.auth.domain.service.impl;

import com.mf.auth.domain.entity.OAuth2Token;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.jackson.io.JacksonSerializer;
import io.jsonwebtoken.security.Keys;

import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JWTGenerator {

	public static final String TOKEN_PREFIX = "msvc_";
	private final JacksonSerializer<Map<String, ?>> serializer;

	public String generate(
		String secret,
		String issuer,
		int expSeconds,
		String uuid,
		Map<String, OAuth2Token> serviceToTokens
	) {
		var claims = serviceToTokens.entrySet().stream()
			.map(record -> Map.entry(TOKEN_PREFIX + record.getKey(), record.getValue()))
			.collect(Collectors.toMap(Entry::getKey, Entry::getValue));

		var expiration = new Date(System.currentTimeMillis() + (long) expSeconds * 1000);
		return Jwts.builder()
			.subject(uuid)
			.issuer(issuer)
			.claims(claims)
			.issuedAt(new Date())
			.expiration(expiration)
			.json(serializer)
			.signWith(Keys.hmacShaKeyFor(secret.getBytes()))
			.compact();
	}
}
