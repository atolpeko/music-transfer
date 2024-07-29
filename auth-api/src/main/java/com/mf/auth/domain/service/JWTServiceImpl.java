package com.mf.auth.domain.service;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.mf.auth.domain.ServiceProperties;
import com.mf.auth.domain.entity.JWT;
import com.mf.auth.domain.entity.OAuth2Token;
import com.mf.auth.domain.entity.Token;
import com.mf.auth.domain.exception.InvalidJWTException;
import com.mf.auth.domain.mapper.OAuth2TokenMapper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.jackson.io.JacksonDeserializer;
import io.jsonwebtoken.jackson.io.JacksonSerializer;
import io.jsonwebtoken.security.Keys;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import java.util.Map.Entry;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;

public class JWTServiceImpl implements JWTService {
	private final ServiceProperties properties;
	private final OAuth2TokenMapper mapper;
	private final JacksonSerializer<Map<String, ?>> serializer;
	private final JacksonDeserializer<Map<String, ?>> deserializer;

	public JWTServiceImpl(
		ServiceProperties properties,
		OAuth2TokenMapper tokenMapper,
		ObjectMapper mapper
	) {
		this.properties = properties;
		this.mapper = tokenMapper;
		this.serializer = new JacksonSerializer<>(mapper);
		this.deserializer = new JacksonDeserializer<>(mapper);
	}

	@Override
	public JWT generate(Token uuid, Map<String, OAuth2Token> serviceToTokens) {
		var token = generateJwt(uuid.getValue(), serviceToTokens);
		return new JWT(token, properties.jwtExpirationSeconds());
	}

	private String generateJwt(String uuid, Map<String, OAuth2Token> serviceToTokens) {
		var key = getSigningKey();
		var claims = serviceToTokens.entrySet().stream()
			.map(record -> Map.entry("msvc_" + record.getKey(), record.getValue()))
			.collect(Collectors.toMap(Entry::getKey, Entry::getValue));

		var expiration = new Date(System.currentTimeMillis()
			+ (long) properties.jwtExpirationSeconds() * 1000);
		return Jwts.builder()
			.subject(uuid)
			.issuer(properties.jwtIssuer())
			.claims(claims)
			.issuedAt(new Date())
			.expiration(expiration)
			.json(serializer)
			.signWith(key)
			.compact();
	}

	private SecretKey getSigningKey() {
		var secret = properties.jwtSecret().getBytes();
		return Keys.hmacShaKeyFor(secret);
	}

	@Override
	public JWT update(Token uuid, JWT jwt, Map<String, OAuth2Token> serviceToTokens) {
		var claims = decrypt(jwt.getValue());
		if (!isValid(claims)) {
			throw new InvalidJWTException("Provided JWT is invalid");
		}

		var tokens = extractTokens(claims);
		tokens.putAll(serviceToTokens);

		var token = generateJwt(uuid.getValue(), tokens);
		return new JWT(token, properties.jwtExpirationSeconds());
	}

	private Claims decrypt(String jwt) {
		try {
			var key = getSigningKey();
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

	@Override
	public boolean isValid(String jwt) {
		try {
			var claims = decrypt(jwt);
			return isValid(claims);
		} catch (Exception e) {
			return false;
		}
	}

	private boolean isValid(Claims claims) {
		return claims.getIssuer().equals(properties.jwtIssuer())
			&& claims.getExpiration().after(new Date());
	}

	private Map<String, OAuth2Token> extractTokens(Claims claims) {
		return claims.entrySet().stream()
			.filter(record -> record.getKey().startsWith("msvc_"))
			.map(record -> Map.entry(record.getKey().replace("msvc_", ""),
				(LinkedHashMap<String, Object>) record.getValue()))
			.map(record -> Map.entry(record.getKey(), mapper.map(record.getValue())))
			.collect(Collectors.toMap(Entry::getKey, Entry::getValue));
	}
}
