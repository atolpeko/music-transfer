package com.mf.api.fixture;

public class JWTValidatorAdapterFixture {

	public static final String URL = "/url";
	public static final String VALID_JWT = "fffdwfgrfdlkwf";
	public static final String INVALID_JWT = "dkfkdfmkvdV";
	public static final String VALID_JWT_URL = URL + "?jwt=" + VALID_JWT;
	public static final String INVALID_JWT_URL = URL + "?jwt=" + INVALID_JWT;
}
