package com.mf.auth.usecase.impl;

import static com.mf.auth.fixture.JWTUseCaseFixture.ACCESS_TOKEN;
import static com.mf.auth.fixture.JWTUseCaseFixture.JWT;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mf.auth.config.UnitTest;
import com.mf.auth.domain.service.JWTService;
import com.mf.auth.port.JWTRepositoryPort;
import com.mf.auth.usecase.exception.AuthorizationException;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@UnitTest
class JWTUseCaseImplTest {

	@InjectMocks
	JWTUseCaseImpl target;

	@Mock
	JWTService jwtService;

	@Mock
	JWTRepositoryPort jwtRepository;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	void testObtainJwtReturnsToken() {
		when(jwtRepository.findValidByAccessToken(ACCESS_TOKEN.getValue()))
			.thenReturn(Optional.of(JWT));

		var jwt = target.obtain(ACCESS_TOKEN.getValue());
		assertEquals(JWT, jwt);
	}

	@Test
	void testObtainJwtInvalidatesAccessTokenAfterUse() {
		when(jwtRepository.findValidByAccessToken(ACCESS_TOKEN.getValue()))
			.thenReturn(Optional.of(JWT))
			.thenReturn(Optional.empty());

		target.obtain(ACCESS_TOKEN.getValue());

		assertThrows(
			AuthorizationException.class,
			() -> target.obtain(ACCESS_TOKEN.getValue())
		);
		verify(jwtRepository, times(1)).updateAccessTokenByJwtId(JWT.getId(), true);
	}

	@Test
	void testObtainJwtThrowsExceptionWhenAccessTokenIsNotPresent() {
		when(jwtRepository.findValidByAccessToken(ACCESS_TOKEN.getValue()))
			.thenReturn(Optional.empty());
		assertThrows(AuthorizationException.class,
			() -> target.obtain(ACCESS_TOKEN.getValue()));
	}

	@Test
	void testPositiveValidation() {
		when(jwtRepository.findValidByValue(JWT.getValue()))
			.thenReturn(Optional.of(JWT));
		when(jwtService.isValid(JWT.getValue())).thenReturn(true);

		var valid = target.isValid(JWT.getValue());
		assertTrue(valid);
	}

	@Test
	void testNegativeValidationWhenJwtIsNotPresent() {
		when(jwtRepository.findValidByValue(JWT.getValue()))
			.thenReturn(Optional.empty());

		var valid = target.isValid(JWT.getValue());
		assertFalse(valid);
	}

	@Test
	void testNegativeValidationWhenJwtIsInvalid() {
		when(jwtRepository.findValidByValue(JWT.getValue()))
			.thenReturn(Optional.of(JWT));
		when(jwtService.isValid(JWT.getValue())).thenReturn(false);

		var valid = target.isValid(JWT.getValue());
		assertFalse(valid);
	}
}