package com.mf.auth.usecase;

import static com.mf.auth.fixture.JWTUseCaseFixture.ACCESS_TOKEN;
import static com.mf.auth.fixture.JWTUseCaseFixture.JWT;
import static com.mf.auth.fixture.JWTUseCaseFixture.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;


import com.mf.auth.config.UnitTest;
import com.mf.auth.domain.entity.Token;
import com.mf.auth.domain.service.JWTService;
import com.mf.auth.port.JWTRepositoryPort;
import com.mf.auth.port.UUIDRepositoryPort;
import com.mf.auth.usecase.exception.AuthorizationException;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;

import java.util.Optional;
import java.util.concurrent.Callable;

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
	UUIDRepositoryPort uuidRepository;

	@Mock
	JWTRepositoryPort jwtRepository;

	@Mock
	CircuitBreaker breaker;

	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		doAnswer(invocation -> {
			Callable<Optional<Token>> task = invocation.getArgument(0);
			return task.call();
		}).when(breaker).executeCallable(any());

		doAnswer(invocation -> {
			Runnable task = invocation.getArgument(0);
			task.run();
			return null;
		}).when(breaker).executeRunnable(any());
	}

	@Test
	void testObtainJwt() {
		when(uuidRepository.findByValue(UUID.getValue()))
			.thenReturn(Optional.of(UUID));
		when(jwtRepository.findByAccessToken(ACCESS_TOKEN.getValue()))
			.thenReturn(Optional.of(JWT));

		var jwt = target.obtain(UUID.getValue(), ACCESS_TOKEN.getValue());

		assertEquals(JWT, jwt);
	}

	@Test
	void testObtainJwtThrowsExceptionWhenUuidIsInvalid() {
		when(uuidRepository.findByValue(UUID.getValue()))
			.thenReturn(Optional.empty());
		assertThrows(AuthorizationException.class,
			() -> target.obtain(UUID.getValue(), ACCESS_TOKEN.getValue()));
	}

	@Test
	void testObtainJwtThrowsExceptionWhenAccessTokenIsNotPresent() {
		when(uuidRepository.findByValue(UUID.getValue()))
			.thenReturn(Optional.of(UUID));
		when(jwtRepository.findByAccessToken(ACCESS_TOKEN.getValue()))
			.thenReturn(Optional.empty());

		assertThrows(AuthorizationException.class,
			() -> target.obtain(UUID.getValue(), ACCESS_TOKEN.getValue()));
	}
}