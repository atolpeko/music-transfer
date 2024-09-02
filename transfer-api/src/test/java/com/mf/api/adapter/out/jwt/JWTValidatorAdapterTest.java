package com.mf.api.adapter.out.jwt;

import static com.mf.api.fixture.JWTValidatorAdapterFixture.DOMAIN;
import static com.mf.api.fixture.JWTValidatorAdapterFixture.URL;
import static com.mf.api.fixture.JWTValidatorAdapterFixture.JWT;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import com.mf.api.config.UnitTest;

import com.mf.api.port.exception.RestException;
import com.mf.queue.entity.Request;
import com.mf.queue.service.RequestQueue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

@UnitTest
class JWTValidatorAdapterTest {

	@InjectMocks
	JWTValidatorAdapter target;

	@Mock
	RequestQueue requestQueue;

	@Mock
	JwtValidatorProperties properties;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.initMocks(this);
		when(properties.domain()).thenReturn(DOMAIN);
		when(properties.jwtValidationUrl()).thenReturn(URL);
	}

	@Test
	void testPositiveValidation() {
		doAnswer(invocation -> {
			var request = (Request<?, ?>) invocation.getArgument(0);
			request.complete(ResponseEntity.status(200).build());
			return null;
		}).when(requestQueue).submit(any());

		var valid = target.isValid(JWT);
		assertTrue(valid);
	}

	@Test
	void testNegativeValidation() {
		doAnswer(invocation -> {
			var request = (Request<?, ?>) invocation.getArgument(0);
			request.complete(ResponseEntity.status(401).build());
			return null;
		}).when(requestQueue).submit(any());

		var valid = target.isValid(JWT);
		assertFalse(valid);
	}

	@Test
	void testThrowsExceptionWhenServiceUnavailableValidation() {
		doAnswer(invocation -> {
			throw new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
		}).when(requestQueue).submit(any());

		assertThrows(RestException.class, () -> target.isValid(JWT));
	}
}
