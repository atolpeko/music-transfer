package com.mf.auth.fixture;

import com.mf.auth.domain.entity.AccessToken;
import com.mf.auth.domain.entity.JWT;
import com.mf.auth.domain.entity.OAuth2Token;
import com.mf.auth.domain.entity.Token;

import java.util.Map;

public class AuthUseCaseFixture {

	public static final String SERVICE_1 = "SERVICE_service_1";
	public static final String SERVICE_2 = "SERVICE_service_2";
	public static final String AUTH_CODE = "code";
	public static final int EXPIRATION_SECONDS = 60 * 60;

	public static final Token UUID = new Token("fererggee", EXPIRATION_SECONDS);

	public static final OAuth2Token OAUTH_2_TOKEN_1 =
		new OAuth2Token("dfdfdf", "reroekr", EXPIRATION_SECONDS);
	public static final OAuth2Token OAUTH_2_TOKEN_2 =
		new OAuth2Token("kfdjfkm", "ekmflke", EXPIRATION_SECONDS);

	public static Map<String, OAuth2Token> TOKEN_MAP = Map.of(
		SERVICE_1, OAUTH_2_TOKEN_1
	);

	public static Map<String, OAuth2Token> NEW_TOKEN_MAP = Map.of(
		SERVICE_2, OAUTH_2_TOKEN_2
	);

	public static final AccessToken
		ACCESS_TOKEN = new AccessToken("rofkdfk", EXPIRATION_SECONDS);

	public static JWT JWT = new JWT("fggrdfsfsgr", EXPIRATION_SECONDS, ACCESS_TOKEN);
	public static JWT NEW_JWT = new JWT("ldfmdf", EXPIRATION_SECONDS, ACCESS_TOKEN);
	public static JWT EXPIRED_JWT = new JWT("ldfmdf", 1, ACCESS_TOKEN);
}
