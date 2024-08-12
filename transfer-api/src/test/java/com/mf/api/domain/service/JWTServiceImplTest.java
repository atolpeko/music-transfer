package com.mf.api.domain.service;

import static com.mf.api.fixture.JWTServiceFixture.CLAIMS;
import static com.mf.api.fixture.JWTServiceFixture.TOKEN_2;
import static com.mf.api.fixture.JWTServiceFixture.JWT;
import static com.mf.api.fixture.JWTServiceFixture.JWT_SECRET;
import static com.mf.api.fixture.JWTServiceFixture.PREFIX;
import static com.mf.api.fixture.JWTServiceFixture.TOKEN_1;
import static com.mf.api.fixture.JWTServiceFixture.TOKEN_MAP;
import static com.mf.api.fixture.JWTServiceFixture.token1Claim;
import static com.mf.api.fixture.JWTServiceFixture.token2Claim;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.mf.api.config.UnitTest;
import com.mf.api.domain.service.exception.InvalidJWTException;
import com.mf.api.domain.service.impl.JWTDecryptor;
import com.mf.api.domain.service.impl.JWTServiceImpl;
import com.mf.api.domain.service.mapper.OAuth2TokenMapper;
import com.mf.api.domain.service.properties.ServiceProperties;

import java.util.HashSet;

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
	JWTDecryptor jwtDecryptor;

	@Mock
	OAuth2TokenMapper tokenMapper;

	@Mock
	ServiceProperties properties;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.initMocks(this);
		when(properties.jwtSecret()).thenReturn(JWT_SECRET);
		when(properties.tokenPrefix()).thenReturn(PREFIX);

		when(tokenMapper.map(token1Claim())).thenReturn(TOKEN_1);
		when(tokenMapper.map(token2Claim())).thenReturn(TOKEN_2);

		when(jwtDecryptor.decrypt(JWT_SECRET, JWT)).thenReturn(CLAIMS);
	}

	@Test
	void testTokenExtraction() {
		var tokens = target.extractTokens(JWT);
		assertEquals(
			new HashSet<>(TOKEN_MAP.values()),
			new HashSet<>(tokens.values())
		);
	}

	@Test
	void testThrowsExceptionWhenJWTIsInvalid() {
		when(jwtDecryptor.decrypt(any(), any())).thenThrow(InvalidJWTException.class);
		assertThrows(
			InvalidJWTException.class,
			() -> target.extractTokens(JWT)
		);
	}
}