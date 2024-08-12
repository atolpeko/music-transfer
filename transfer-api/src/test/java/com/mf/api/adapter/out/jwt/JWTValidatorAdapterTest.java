package com.mf.api.adapter.out.jwt;

import static com.mf.api.fixture.JWTValidatorAdapterFixture.INVALID_JWT;

import static com.mf.api.fixture.JWTValidatorAdapterFixture.REQUEST_FOR_INVALID_JWT;
import static com.mf.api.fixture.JWTValidatorAdapterFixture.REQUEST_FOR_VALID_JWT;
import static com.mf.api.fixture.JWTValidatorAdapterFixture.URL;
import static com.mf.api.fixture.JWTValidatorAdapterFixture.VALID_JWT;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.mf.api.config.UnitTest;
import com.mf.api.util.RestClient;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestClientException;

@UnitTest
class JWTValidatorAdapterTest {

	@InjectMocks
	JWTValidatorAdapter target;

	@Mock
	RestClient restClient;

	@Mock
	JwtValidatorProperties properties;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.initMocks(this);
		when(properties.jwtValidationUrl()).thenReturn(URL);
		doAnswer(invocation -> "401")
			.when(restClient).request(REQUEST_FOR_VALID_JWT);
		doThrow(RestClientException.class)
			.when(restClient).request(REQUEST_FOR_INVALID_JWT);
	}

	@Test
	void testPositiveValidation() {
		var valid = target.isValid(VALID_JWT);
		assertTrue(valid);
	}

	@Test
	void testNegativeValidation() {
		var valid = target.isValid(INVALID_JWT);
		assertFalse(valid);
	}
}
