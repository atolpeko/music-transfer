package com.mf.auth.fixture;

import com.mf.auth.domain.entity.JWT;
import com.mf.auth.domain.entity.OAuth2Token;
import com.mf.auth.domain.entity.Token;

import java.util.Map;

public class JWTServiceFixture {
	public static final String JWT_ISSUER = "JWT_ISSUER";
	public static final String JWT_SECRET = "ajoprtfghloasjktpqwobasvjtopfvlfgkp";
	public static final int JWT_EXPIRATION_SECONDS = 60 * 60;

	public static final Token UUID = new Token("fererggee", JWT_EXPIRATION_SECONDS);

	public static Map<String, OAuth2Token> TOKEN_MAP = Map.of(
		"SERVICE_1", new OAuth2Token("dfdfdf", "reroekr", JWT_EXPIRATION_SECONDS)
	);
	public static Map<String, OAuth2Token> NEW_TOKEN_MAP = Map.of(
		"SERVICE_2", new OAuth2Token("kldfjk", "lksjsfk", JWT_EXPIRATION_SECONDS),
		"SERVICE_3", new OAuth2Token("oiujdk", "powflwm", JWT_EXPIRATION_SECONDS)
	);

	public static JWT INVALID_JWT = new JWT("fggrgr", JWT_EXPIRATION_SECONDS);
}
