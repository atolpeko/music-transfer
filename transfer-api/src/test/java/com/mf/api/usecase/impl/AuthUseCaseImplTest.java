package com.mf.api.usecase.impl;

import static com.mf.api.fixture.AuthUseCaseFixture.AUTH_TOKEN;
import static com.mf.api.fixture.AuthUseCaseFixture.TOKEN_MAP;
import static com.mf.api.fixture.AuthUseCaseFixture.JWT;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.Mockito.when;

import com.mf.api.config.UnitTest;
import com.mf.api.domain.service.JWTService;
import com.mf.api.domain.service.exception.InvalidJWTException;
import com.mf.api.port.JWTValidatorPort;
import com.mf.api.usecase.exception.AuthorizationException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@UnitTest
class AuthUseCaseImplTest {

	@InjectMocks
	AuthUseCaseImpl target;

	@Mock
	JWTValidatorPort jwtValidator;

	@Mock
	JWTService jwtService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	void testExtractsTokens() {
		when(jwtValidator.isValid(JWT)).thenReturn(true);
		when(jwtService.extractTokens(JWT)).thenReturn(TOKEN_MAP);

		var result = target.extractTokens(AUTH_TOKEN);
		TOKEN_MAP.keySet().forEach(key -> assertNotNull(result.get(key)));
	}

	@Test
	void testThrowsExceptionWhenJWTIsInvalid() {
		when(jwtValidator.isValid(JWT)).thenReturn(false);
		assertThrows(
			AuthorizationException.class,
			() -> target.extractTokens(AUTH_TOKEN)
		);
	}

	@Test
	void testThrowsExceptionWhenJWTIsMalformed() {
		when(jwtService.extractTokens(JWT)).thenThrow(InvalidJWTException.class);
		assertThrows(
			AuthorizationException.class,
			() -> target.extractTokens(AUTH_TOKEN)
		);
	}
}