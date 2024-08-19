package com.mf.auth.domain.service.impl;

import static com.mf.auth.fixture.JWTServiceFixture.CLAIMS;
import static com.mf.auth.fixture.JWTServiceFixture.CLAIMS_WITH_EXPIRED_TOKENS;
import static com.mf.auth.fixture.JWTServiceFixture.EXPIRATION_SECONDS;
import static com.mf.auth.fixture.JWTServiceFixture.EXPIRED_CLAIMS;
import static com.mf.auth.fixture.JWTServiceFixture.EXPIRED_TOKEN;
import static com.mf.auth.fixture.JWTServiceFixture.INVALID_CLAIMS;
import static com.mf.auth.fixture.JWTServiceFixture.JWT;
import static com.mf.auth.fixture.JWTServiceFixture.JWT_ISSUER;
import static com.mf.auth.fixture.JWTServiceFixture.JWT_SECRET;
import static com.mf.auth.fixture.JWTServiceFixture.NEW_TOKEN_MAP;
import static com.mf.auth.fixture.JWTServiceFixture.TOKEN_1;
import static com.mf.auth.fixture.JWTServiceFixture.TOKEN_2;
import static com.mf.auth.fixture.JWTServiceFixture.TOKEN_MAP;
import static com.mf.auth.fixture.JWTServiceFixture.UUID;

import static com.mf.auth.fixture.JWTServiceFixture.expiredTokenClaim;
import static com.mf.auth.fixture.JWTServiceFixture.token1Claim;
import static com.mf.auth.fixture.JWTServiceFixture.token2Claim;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.mf.auth.config.UnitTest;
import com.mf.auth.domain.service.properties.ServiceProperties;
import com.mf.auth.domain.service.exception.InvalidJWTException;
import com.mf.auth.domain.service.impl.mapper.OAuth2TokenMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@UnitTest
class JWTServiceImplTest {

	@InjectMocks
	JWTServiceImpl target;

	@Mock
	JWTGenerator jwtGenerator;

	@Mock
	JWTDecryptor jwtDecryptor;

	@Mock
	OAuth2TokenMapper tokenMapper;

	@Mock
	ServiceProperties properties;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.initMocks(this);
		when(properties.jwtIssuer()).thenReturn(JWT_ISSUER);
		when(properties.jwtSecret()).thenReturn(JWT_SECRET);
		when(properties.jwtExpirationSeconds()).thenReturn(EXPIRATION_SECONDS);

		when(tokenMapper.map(token1Claim())).thenReturn(TOKEN_1);
		when(tokenMapper.map(token2Claim())).thenReturn(TOKEN_2);
		when(tokenMapper.map(expiredTokenClaim())).thenReturn(EXPIRED_TOKEN);

		when(jwtGenerator.generate(
			JWT_SECRET,
			JWT_ISSUER,
			EXPIRATION_SECONDS,
			UUID.getValue(),
			TOKEN_MAP
		)).thenReturn(JWT.getValue());
	}

	@Test
	void testGeneration() {
		var token = target.generate(UUID, TOKEN_MAP);
		assertEquals(JWT.getValue(), token.getValue());
	}

	@Test
	void testUpdate() {
		when(jwtDecryptor.decrypt(any(), any())).thenReturn(CLAIMS);

		var token = target.generate(UUID, TOKEN_MAP);
		var updated = target.update(UUID, token, NEW_TOKEN_MAP);
		assertTrue(target.isValid(updated.getValue()));
	}

	@Test
	void testPositiveValidation() {
		when(jwtDecryptor.decrypt(any(), any())).thenReturn(CLAIMS);

		var valid = target.isValid(JWT.getValue());
		assertTrue(valid);
	}

	@Test
	void testNegativeValidationWhenJWTIsExpired() {
		when(jwtDecryptor.decrypt(any(), any())).thenReturn(EXPIRED_CLAIMS);

		var valid = target.isValid(JWT.getValue());
		assertFalse(valid);
	}

	@Test
	void testNegativeValidationWhenTokensAreExpired() {
		when(jwtDecryptor.decrypt(any(), any())).thenReturn(CLAIMS_WITH_EXPIRED_TOKENS);

		var valid = target.isValid(JWT.getValue());
		assertFalse(valid);
	}

	@Test
	void testThrowsExceptionWhenJWTIsInvalid() {
		when(jwtDecryptor.decrypt(any(), any())).thenReturn(INVALID_CLAIMS);
		assertThrows(
			InvalidJWTException.class,
			() -> target.update(UUID, JWT, NEW_TOKEN_MAP)
		);
	}

	@Test
	void testThrowsExceptionWhenJWTIsExpired() {
		when(jwtDecryptor.decrypt(any(), any())).thenReturn(EXPIRED_CLAIMS);
		assertThrows(
			InvalidJWTException.class,
			() -> target.update(UUID, JWT, NEW_TOKEN_MAP)
		);
	}
}
