package com.mf.api.fixture;

import com.mf.api.domain.entity.OAuth2Token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.impl.DefaultClaimsBuilder;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JWTServiceFixture {

	public static final String JWT ="dgdfjhdjfklfjwef";
	public static final String JWT_SECRET = "ajoprtfghloasjktpqwobasvjtopfvlfgkp";
	public static final LocalDateTime EXPIRES = LocalDateTime.now().plusHours(1);
	public static final String PREFIX = "msvc_";

	public static final String SERVICE_1 = PREFIX + "SERVICE_1";
	public static final String SERVICE_2 = PREFIX + "SERVICE_2";

	public static final OAuth2Token TOKEN_1 = new OAuth2Token("dfdfdf", "reroekr", EXPIRES);
	public static final OAuth2Token TOKEN_2 = new OAuth2Token("dgdgcf", "tdgxgge", EXPIRES);

	public static final Map<String, OAuth2Token> TOKEN_MAP = Map.of(
		SERVICE_1, TOKEN_1,
		SERVICE_2, TOKEN_2
	);

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

	public static Claims CLAIMS = new DefaultClaimsBuilder()
		.expiration(new Date(System.currentTimeMillis() + 10000))
		.add(SERVICE_1, token1Claim())
		.add(SERVICE_2, token2Claim())
		.build();
}
