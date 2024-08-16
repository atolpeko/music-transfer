package com.mf.auth.fixture;

import com.mf.auth.domain.entity.JWT;
import com.mf.auth.domain.entity.OAuth2Token;
import com.mf.auth.domain.entity.Token;
import com.mf.auth.domain.service.impl.JWTGenerator;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.impl.DefaultClaimsBuilder;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JWTServiceFixture {

	public static final String JWT_ISSUER = "JWT_ISSUER";
	public static final String JWT_SECRET = "ajoprtfghloasjktpqwobasvjtopfvlfgkp";
	public static final int EXPIRATION_SECONDS = 60 * 60;

	public static final JWT JWT = new JWT("dgdfjhdjfklfjwef", EXPIRATION_SECONDS);

	public static final Token UUID = new Token("fererggee", EXPIRATION_SECONDS);

	public static final String SERVICE_1 = JWTGenerator.TOKEN_PREFIX + "SERVICE_1";
	public static final String SERVICE_2 = JWTGenerator.TOKEN_PREFIX + "SERVICE_2";

	public static final OAuth2Token TOKEN_1 = new OAuth2Token("dfdfdf", "reroekr", EXPIRATION_SECONDS);
	public static final OAuth2Token TOKEN_2 = new OAuth2Token("dgdgcf", "tdgxgge", EXPIRATION_SECONDS);
	public static final OAuth2Token EXPIRED_TOKEN =
		new OAuth2Token("dgdgcf", "tdgxgge", LocalDateTime.now().minusSeconds(10));

	public static final Map<String, OAuth2Token> TOKEN_MAP = Map.of(
		SERVICE_1, TOKEN_1
	);

	public static final Map<String, OAuth2Token> NEW_TOKEN_MAP = Map.of(
		SERVICE_2, TOKEN_2
	);

	public static final Claims CLAIMS = new DefaultClaimsBuilder()
		.issuer(JWT_ISSUER)
		.subject(UUID.getValue())
		.add(SERVICE_1, token1Claim())
		.expiration(new Date(System.currentTimeMillis() + 10000))
		.build();

	public static final Claims INVALID_CLAIMS = new DefaultClaimsBuilder()
		.issuer("invalid")
		.subject(UUID.getValue())
		.add(SERVICE_1, token1Claim())
		.expiration(new Date(System.currentTimeMillis() + 10000))
		.build();

	public static final Claims EXPIRED_CLAIMS = new DefaultClaimsBuilder()
		.issuer(JWT_ISSUER)
		.subject(UUID.getValue())
		.add(SERVICE_1, token1Claim())
		.expiration(new Date())
		.build();

	public static final Claims CLAIMS_WITH_EXPIRED_TOKENS = new DefaultClaimsBuilder()
		.issuer(JWT_ISSUER)
		.subject(UUID.getValue())
		.add(SERVICE_1, expiredTokenClaim())
		.expiration(new Date(System.currentTimeMillis() + 10000))
		.build();

	public static LinkedHashMap<String, Object> token1Claim() {
		var token = new LinkedHashMap<String, Object>();
		token.put("value", TOKEN_1.getValue());
		token.put("refreshToken", TOKEN_1.getRefreshToken());
		token.put("expiresAt", List.of(2024, 8, 12, 18, 57, 12, 14));

		return token;
	}

	public static LinkedHashMap<String, Object> token2Claim() {
		var token = new LinkedHashMap<String, Object>();
		token.put("value", TOKEN_2.getValue());
		token.put("refreshToken", TOKEN_2.getRefreshToken());
		token.put("expiresAt", List.of(2024, 8, 12, 18, 57, 12, 14));

		return token;
	}

	public static LinkedHashMap<String, Object> expiredTokenClaim() {
		var token = new LinkedHashMap<String, Object>();
		token.put("value", EXPIRED_TOKEN.getValue());
		token.put("refreshToken", EXPIRED_TOKEN.getRefreshToken());
		token.put("expiresAt", List.of(2024, 8, 12, 18, 57, 12, 14));

		return token;
	}
}
