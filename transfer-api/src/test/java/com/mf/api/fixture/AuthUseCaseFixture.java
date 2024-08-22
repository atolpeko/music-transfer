package com.mf.api.fixture;

import com.mf.api.domain.entity.OAuth2Token;

import java.time.LocalDateTime;
import java.util.Map;

public class AuthUseCaseFixture {

	public static final String AUTH_TOKEN = "Bearer dfgfmdkjfkndmlfgfdf";
	public static final String JWT = "dfgfmdkjfkndmlfgfdf";

	public static final Map<String, OAuth2Token> TOKEN_MAP = Map.of(
		"SERVICE_1", new OAuth2Token("dfdfdf", "reroekr", LocalDateTime.MAX),
		"SERVICE_2", new OAuth2Token("dfdfdf", "reroekr", LocalDateTime.MAX)
	);
}
