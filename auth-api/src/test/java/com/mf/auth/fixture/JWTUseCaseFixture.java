package com.mf.auth.fixture;

import com.mf.auth.domain.entity.JWT;
import com.mf.auth.domain.entity.Token;

public class JWTUseCaseFixture {
	public static final int EXPIRATION_SECONDS = 60 * 60;
	public static final Token UUID = new Token("fererggee", EXPIRATION_SECONDS);
	public static final Token ACCESS_TOKEN = new Token("rofkdfk", EXPIRATION_SECONDS);
	public static JWT JWT = new JWT("fggrdfsfsgr", EXPIRATION_SECONDS, ACCESS_TOKEN);
}
