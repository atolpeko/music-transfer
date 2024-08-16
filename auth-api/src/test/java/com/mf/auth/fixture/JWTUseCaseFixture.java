package com.mf.auth.fixture;

import com.mf.auth.domain.entity.AccessToken;
import com.mf.auth.domain.entity.JWT;

public class JWTUseCaseFixture {

	public static final int EXPIRATION_SECONDS = 60 * 60;

	public static final AccessToken ACCESS_TOKEN =
		new AccessToken("rofkdfk", EXPIRATION_SECONDS);

	public static JWT JWT = new JWT("fggrdfsfsgr", EXPIRATION_SECONDS, ACCESS_TOKEN);
}
