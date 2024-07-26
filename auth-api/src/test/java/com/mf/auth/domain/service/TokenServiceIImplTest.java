package com.mf.auth.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.mf.auth.config.UnitTest;

import java.time.LocalDateTime;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@UnitTest
class TokenServiceIImplTest {

	TokenServiceIImpl target;

	@BeforeEach
	void setUp() {
		target = new TokenServiceIImpl();
	}

	@Test
	void testExpirationSet() {
		var token = target.generate(2);
		var now = LocalDateTime.now();
		assertTrue(token.getExpiresAt().isAfter(now));
	}

	@Test
	void testUniqueness() {
		var count = 1000000;
		var tokens = IntStream.range(0, count)
			.mapToObj(i -> target.generate(200))
			.collect(Collectors.toSet());

		assertEquals(count, tokens.size());
	}

	@Test
	void testValid() {
		var token = target.generate(60);
		assertTrue(token.isValid());
	}
}