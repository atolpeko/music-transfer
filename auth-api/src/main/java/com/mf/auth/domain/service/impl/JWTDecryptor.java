package com.mf.auth.domain.service.impl;

import com.mf.auth.domain.service.exception.InvalidJWTException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.jackson.io.JacksonDeserializer;
import io.jsonwebtoken.security.Keys;

import java.util.Map;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JWTDecryptor {

	private final JacksonDeserializer<Map<String, ?>> deserializer;

	public Claims decrypt(String secret, String jwt) {
		try {
			var key = Keys.hmacShaKeyFor(secret.getBytes());
			return Jwts.parser()
				.json(deserializer)
				.verifyWith(key)
				.build()
				.parseSignedClaims(jwt)
				.getPayload();
		} catch (SecurityException e) {
			throw new InvalidJWTException("Invalid JWT signature", e);
		} catch (ExpiredJwtException e) {
			throw new InvalidJWTException("JWT token is expired", e);
		} catch (MalformedJwtException e) {
			throw new InvalidJWTException("Invalid JWT token", e);
		} catch (UnsupportedJwtException e) {
			throw new InvalidJWTException("JWT token is unsupported", e);
		} catch (IllegalArgumentException e) {
			throw new InvalidJWTException("JWT claims string is empty", e);
		} catch (Exception e) {
			throw new InvalidJWTException("Invalid JWT", e);
		}
	}
}
