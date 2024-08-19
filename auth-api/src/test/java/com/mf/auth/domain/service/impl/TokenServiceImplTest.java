package com.mf.auth.domain.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.mf.auth.config.UnitTest;
import com.mf.auth.domain.service.properties.ServiceProperties;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@UnitTest
class TokenServiceImplTest {

	@InjectMocks
	TokenServiceImpl target;

	@Mock
	ServiceProperties properties;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.initMocks(this);
		when(properties.uuidExpirationSeconds()).thenReturn(100);
		when(properties.accessTokenExpirationSeconds()).thenReturn(100);
	}

	@Test
	void testUuidGeneration() {
		var token = target.generateUuid();
		assertTrue(token.isValid());
	}

	@Test
	void testAccessTokenGeneration() {
		var token = target.generateAccessToken();
		assertTrue(token.isValid());
	}

	@Test
	void testUuidUniqueness() {
		var count = 100000;
		var tokens = IntStream.range(0, count)
			.mapToObj(i -> target.generateUuid())
			.collect(Collectors.toSet());

		assertEquals(count, tokens.size());
	}

	@Test
	void testAccessTokenUniqueness() {
		var count = 100000;
		var tokens = IntStream.range(0, count)
			.mapToObj(i -> target.generateAccessToken())
			.collect(Collectors.toSet());

		assertEquals(count, tokens.size());
	}
}