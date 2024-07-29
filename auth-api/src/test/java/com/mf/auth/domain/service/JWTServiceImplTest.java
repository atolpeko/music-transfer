package com.mf.auth.domain.service;

import static com.mf.auth.fixture.JWTServiceFixture.INVALID_JWT;
import static com.mf.auth.fixture.JWTServiceFixture.JWT_EXPIRATION_SECONDS;
import static com.mf.auth.fixture.JWTServiceFixture.JWT_ISSUER;
import static com.mf.auth.fixture.JWTServiceFixture.JWT_SECRET;
import static com.mf.auth.fixture.JWTServiceFixture.NEW_TOKEN_MAP;
import static com.mf.auth.fixture.JWTServiceFixture.TOKEN_MAP;
import static com.mf.auth.fixture.JWTServiceFixture.UUID;

import static org.awaitility.Awaitility.await;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import com.mf.auth.config.UnitTest;
import com.mf.auth.domain.ServiceProperties;
import com.mf.auth.domain.exception.InvalidJWTException;
import com.mf.auth.domain.mapper.OAuth2TokenMapper;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@UnitTest
class JWTServiceImplTest {
	JWTServiceImpl target;

	@Mock
	ServiceProperties properties;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.initMocks(this);
		when(properties.jwtIssuer()).thenReturn(JWT_ISSUER);
		when(properties.jwtSecret()).thenReturn(JWT_SECRET);
		when(properties.jwtExpirationSeconds()).thenReturn(JWT_EXPIRATION_SECONDS);

		var objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());

		var tokenMapper = new OAuth2TokenMapper();

		target = new JWTServiceImpl(properties, tokenMapper, objectMapper);
	}

	@Test
	void testGeneration() {
		var token = target.generate(UUID, TOKEN_MAP);
		assertTrue(target.isValid(token.getValue()));
	}

	@Test
	void testUpdate() {
		var token = target.generate(UUID, TOKEN_MAP);
		var updated = target.update(UUID, token, NEW_TOKEN_MAP);
		assertTrue(target.isValid(updated.getValue()));
	}

	@Test
	void testThrowsExceptionWhenTokenIsInvalid() {
		assertThrows(
			InvalidJWTException.class,
			() -> target.update(UUID, INVALID_JWT, NEW_TOKEN_MAP)
		);
	}

	@Test
	void testThrowsExceptionWhenTokenIsExpired() {
		when(properties.jwtExpirationSeconds()).thenReturn(1);

		var token = target.generate(UUID, TOKEN_MAP);
		await().pollDelay(1, TimeUnit.SECONDS).until(() -> true);

		assertThrows(
			InvalidJWTException.class,
			() -> target.update(UUID, token, NEW_TOKEN_MAP)
		);
	}
}
