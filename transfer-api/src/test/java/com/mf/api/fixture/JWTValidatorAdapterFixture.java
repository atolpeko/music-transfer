package com.mf.api.fixture;

import com.mf.api.util.Param;
import com.mf.api.util.RestRequest;

import java.util.List;

import org.springframework.http.HttpMethod;

public class JWTValidatorAdapterFixture {

	public static final String URL = "url";
	public static final String VALID_JWT = "fffdwfgrfdlkwf";
	public static final String INVALID_JWT = "dkfkdfmkvdV";

	public static final RestRequest REQUEST_FOR_VALID_JWT =
		RestRequest.builder()
			.url(URL)
			.method(HttpMethod.GET)
			.params(List.of(Param.of("jwt", VALID_JWT)))
			.build();

	public static final RestRequest REQUEST_FOR_INVALID_JWT =
		RestRequest.builder()
			.url(URL)
			.method(HttpMethod.GET)
			.params(List.of(Param.of("jwt", INVALID_JWT)))
			.build();
}
