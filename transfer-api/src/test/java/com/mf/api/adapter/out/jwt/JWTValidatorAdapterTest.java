package com.mf.api.adapter.out.jwt;

import static com.mf.api.fixture.JWTValidatorAdapterFixture.DOMAIN;
import static com.mf.api.fixture.JWTValidatorAdapterFixture.INVALID_JWT;
import static com.mf.api.fixture.JWTValidatorAdapterFixture.INVALID_JWT_URL;
import static com.mf.api.fixture.JWTValidatorAdapterFixture.URL;
import static com.mf.api.fixture.JWTValidatorAdapterFixture.VALID_JWT;
import static com.mf.api.fixture.JWTValidatorAdapterFixture.VALID_JWT_URL;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.Mockito.when;

import com.mf.api.config.UnitTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@UnitTest
class JWTValidatorAdapterTest {

	@InjectMocks
	JWTValidatorAdapter target;

	@Mock
	RestTemplate restTemplate;

	@Mock
	JwtValidatorProperties properties;

	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		when(properties.domain()).thenReturn(DOMAIN);
		when(properties.jwtValidationUrl()).thenReturn(URL);
		when(restTemplate.getForEntity(DOMAIN + VALID_JWT_URL, Void.class))
			.thenReturn(ResponseEntity.status(200).build());
		when(restTemplate.getForEntity(DOMAIN + INVALID_JWT_URL, Void.class))
			.thenReturn(ResponseEntity.status(401).build());
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
