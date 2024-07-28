package com.mf.auth.usecase;

import static com.mf.auth.fixture.AuthUseCaseFixture.ACCESS_TOKEN;
import static com.mf.auth.fixture.AuthUseCaseFixture.AUTH_CODE;
import static com.mf.auth.fixture.AuthUseCaseFixture.EXPIRED_JWT;
import static com.mf.auth.fixture.AuthUseCaseFixture.JWT;
import static com.mf.auth.fixture.AuthUseCaseFixture.NEW_JWT;
import static com.mf.auth.fixture.AuthUseCaseFixture.NEW_TOKEN_MAP;
import static com.mf.auth.fixture.AuthUseCaseFixture.OAUTH_2_TOKEN_1;
import static com.mf.auth.fixture.AuthUseCaseFixture.OAUTH_2_TOKEN_2;
import static com.mf.auth.fixture.AuthUseCaseFixture.SERVICE_1;
import static com.mf.auth.fixture.AuthUseCaseFixture.SERVICE_2;
import static com.mf.auth.fixture.AuthUseCaseFixture.TOKEN_MAP;
import static com.mf.auth.fixture.AuthUseCaseFixture.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mf.auth.config.UnitTest;
import com.mf.auth.domain.entity.Token;
import com.mf.auth.domain.service.JWTService;
import com.mf.auth.domain.service.SymmetricEncryptionService;
import com.mf.auth.domain.service.TokenService;
import com.mf.auth.port.JWTRepositoryPort;
import com.mf.auth.port.MusicServicePort;
import com.mf.auth.port.UUIDRepositoryPort;
import com.mf.auth.usecase.exception.AuthorizationException;
import com.mf.auth.usecase.exception.RepositoryAccessException;
import com.mf.auth.usecase.valueobject.ServiceMap;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;

import java.util.Optional;
import java.util.concurrent.Callable;

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
	TokenService tokenService;

	@Mock
	JWTService jwtService;

	@Mock
	SymmetricEncryptionService encryptionService;

	@Mock
	UUIDRepositoryPort uuidRepository;

	@Mock
	JWTRepositoryPort jwtRepository;

	@Mock
	ServiceMap serviceMap;

	@Mock
	CircuitBreaker breaker;

	@Mock
	MusicServicePort musicService;

	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		when(serviceMap.get(SERVICE_1)).thenReturn(musicService);
		when(serviceMap.get(SERVICE_2)).thenReturn(musicService);

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
	void testUuidGeneration() {
		when(tokenService.generateUuid()).thenReturn(UUID);

		var token = target.generateUuid();

		assertTrue(token.isValid());
		verify(uuidRepository, times(1)).save(UUID);
	}

	@Test
	void testUuidGenerationThrowsExceptionIfDbUnavailable() {
		when(tokenService.generateUuid()).thenReturn(UUID);
		doThrow(RepositoryAccessException.class)
			.when(uuidRepository).save(UUID);

		assertThrows(RepositoryAccessException.class, () -> target.generateUuid());
	}

	@Test
	void testAuth() {
		when(uuidRepository.findValidByValue(UUID.getValue()))
			.thenReturn(Optional.of(UUID));
		when(jwtService.isValid(JWT.getValue())).thenReturn(true);
		when(musicService.oauth2ExchangeCode(AUTH_CODE)).thenReturn(OAUTH_2_TOKEN_1);
		when(jwtService.generate(UUID, TOKEN_MAP)).thenReturn(JWT);
		when(tokenService.generateAccessToken()).thenReturn(ACCESS_TOKEN);

		var token = target.auth(UUID.getValue(), SERVICE_1, AUTH_CODE);

		assertEquals(token, ACCESS_TOKEN);
		verify(jwtRepository, times(1)).save(JWT);
	}

	@Test
	void testAuthWithUpdateJwt() {
		when(uuidRepository.findValidByValue(UUID.getValue()))
			.thenReturn(Optional.of(UUID));
		when(jwtRepository.findValidByValue(JWT.getValue()))
			.thenReturn(Optional.of(JWT));
		when(jwtService.isValid(JWT.getValue())).thenReturn(true);
		when(musicService.oauth2ExchangeCode(AUTH_CODE)).thenReturn(OAUTH_2_TOKEN_2);
		when(jwtService.update(UUID, JWT, NEW_TOKEN_MAP)).thenReturn(NEW_JWT);
		when(tokenService.generateAccessToken()).thenReturn(ACCESS_TOKEN);

		var token = target.auth(UUID.getValue(), JWT.getValue(), SERVICE_2, AUTH_CODE);

		assertEquals(token, ACCESS_TOKEN);
		verify(jwtRepository, times(1)).save(NEW_JWT);
		verify(jwtRepository, times(1)).deleteById(JWT.getId());
	}

	@Test
	void testAuthThrowsExceptionIfDbUnavailable() {
		doThrow(RepositoryAccessException.class)
			.when(uuidRepository).findValidByValue(UUID.getValue());
		assertThrows(RepositoryAccessException.class,
			() -> target.auth(UUID.getValue(), JWT.getValue(), SERVICE_1, AUTH_CODE));
	}

	@Test
	void testAuthThrowsExceptionIfUuidIsInvalid() {
		when(uuidRepository.findValidByValue(UUID.getValue()))
			.thenReturn(Optional.empty());
		assertThrows(AuthorizationException.class,
			() -> target.auth(UUID.getValue(), JWT.getValue(), SERVICE_1, AUTH_CODE));
	}

	@Test
	void testAuthThrowsExceptionIfJwtIsNotPresent() {
		when(uuidRepository.findValidByValue(UUID.getValue()))
			.thenReturn(Optional.of(UUID));
		when(jwtRepository.findValidByValue(JWT.getValue()))
			.thenReturn(Optional.empty());

		assertThrows(AuthorizationException.class,
			() -> target.auth(UUID.getValue(), JWT.getValue(), SERVICE_1, AUTH_CODE));
	}

	@Test
	void testAuthThrowsExceptionIfJwtIsExpired() {
		when(uuidRepository.findValidByValue(UUID.getValue()))
			.thenReturn(Optional.of(UUID));
		when(jwtRepository.findValidByValue(JWT.getValue()))
			.thenReturn(Optional.of(EXPIRED_JWT));

		assertThrows(AuthorizationException.class,
			() -> target.auth(UUID.getValue(), JWT.getValue(), SERVICE_1, AUTH_CODE));
	}

	@Test
	void testAuthThrowsExceptionIfJwtIsNotValid() {
		when(uuidRepository.findValidByValue(UUID.getValue()))
			.thenReturn(Optional.of(UUID));
		when(jwtRepository.findValidByValue(JWT.getValue()))
			.thenReturn(Optional.of(JWT));
		when(jwtService.isValid(JWT.getValue())).thenReturn(false);

		assertThrows(AuthorizationException.class,
			() -> target.auth(UUID.getValue(), JWT.getValue(), SERVICE_1, AUTH_CODE));
	}
 }
