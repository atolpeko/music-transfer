package com.mf.auth.usecase;

import com.mf.auth.domain.entity.AccessToken;
import com.mf.auth.domain.entity.JWT;
import com.mf.auth.domain.entity.Token;
import com.mf.auth.domain.service.JWTService;
import com.mf.auth.domain.service.SymmetricEncryptionService;
import com.mf.auth.domain.service.TokenService;
import com.mf.auth.port.JWTRepositoryPort;
import com.mf.auth.port.UUIDRepositoryPort;
import com.mf.auth.port.exception.DataAccessException;
import com.mf.auth.port.exception.DataModificationException;
import com.mf.auth.usecase.exception.AuthorizationException;
import com.mf.auth.usecase.exception.RepositoryAccessException;
import com.mf.auth.usecase.exception.UseCaseException;
import com.mf.auth.usecase.valueobject.ServiceMap;
import com.mf.auth.domain.entity.OAuth2Token;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;

import java.util.Map;
import java.util.Optional;

import java.util.concurrent.Callable;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Transactional
@RequiredArgsConstructor
public class AuthUseCaseImpl implements AuthUseCase {
	private final TokenService tokenService;
	private final JWTService jwtService;
	private final SymmetricEncryptionService encryptionService;

	private final UUIDRepositoryPort uuidRepository;
	private final JWTRepositoryPort jwtRepository;

	private final CircuitBreaker breaker;
	private final ServiceMap serviceMap;

	@Override
	public Token generateUuid() {
		try {
			var uuid = tokenService.generateUuid();
			breaker.executeRunnable(() -> uuidRepository.save(uuid));
			log.debug("Generated UUID for a new user");
			return uuid;
		} catch (UseCaseException e) {
			throw e;
		} catch (DataAccessException e) {
			throw new RepositoryAccessException("Failed to access DB", e);
		} catch (Exception e) {
			throw new RepositoryAccessException("Failed to save UUID", e);
		}
	}

	@Override
	public String encryptWithSecret(String toEncrypt, String secret) {
		return encryptionService.encrypt(toEncrypt, secret);
	}

	@Override
	public String decryptWithSecret(String toDecrypt, String secret) {
		return encryptionService.decrypt(toDecrypt, secret);
	}

	@Override
	public AccessToken auth(String uuidValue, String service, String authCode) {
		try {
			var uuid = findValidUuid(uuidValue);
			var token = exchangeOAuth2Code(service, authCode);

			log.debug("Generating JWT with {} OAuth2 token", service);
			var jwt = jwtService.generate(uuid, Map.of(service, token));
			var accessToken = tokenService.generateAccessToken();
			jwt.setAccessToken(accessToken);

			breaker.executeRunnable(() -> jwtRepository.save(jwt));
			log.info("Authenticated into {}", service);
			return accessToken;
		} catch (UseCaseException e) {
			throw e;
		} catch (DataModificationException e) {
			throw new RepositoryAccessException("Failed to save JWT", e);
		} catch (Exception e) {
			throw new RepositoryAccessException("Failed to access DB", e);
		}
	}

	private Token findValidUuid(String uuidValue) throws Exception {
		var uuid = breaker.executeCallable(
			() -> uuidRepository.findValidByValue(uuidValue)
		);

		if (uuid.isEmpty() || !uuid.get().isValid()) {
			throw new AuthorizationException("UUID is invalid");
		}

		return uuid.get();
	}

	private OAuth2Token exchangeOAuth2Code(String service, String authCode) {
		try {
			log.debug("Obtaining OAUth2 token from {}", service);
			var musicSvc = serviceMap.get(service);
			return musicSvc.oauth2ExchangeCode(authCode);
		} catch (Exception e) {
			throw new AuthorizationException(e.getMessage(), e);
		}
	}

	@Override
	public AccessToken auth(
		String uuidValue,
		String jwt,
		String service,
		String authCode
	) {
		try {
			var uuid = findValidUuid(uuidValue);

			log.debug("Looking for an existing JWT");
			Callable<Optional<JWT>> find = () -> jwtRepository.findValidByValue(jwt);
			var oldJwt = breaker.executeCallable(find)
				.orElseThrow(() -> new AuthorizationException("No such JWT " + jwt));
			if (!oldJwt.isValid()) {
				throw new AuthorizationException("JWT expired");
			} else if (!jwtService.isValid(oldJwt.getValue())) {
				throw new AuthorizationException("JWT is invalid");
			}

			var token = exchangeOAuth2Code(service, authCode);

			log.debug("Updating JWT adding {} OAuth2 token", service);
			var newJwt = jwtService.update(uuid, oldJwt, Map.of(service, token));
			var accessToken = tokenService.generateAccessToken();
			newJwt.setAccessToken(accessToken);

			breaker.executeRunnable(() -> jwtRepository.deleteById(oldJwt.getId()));
			breaker.executeRunnable(() -> jwtRepository.save(newJwt));
			log.info("Authenticated into {}", service);
			return accessToken;
		} catch (UseCaseException e) {
			throw e;
		} catch (DataModificationException e) {
			throw new RepositoryAccessException("Failed to save JWT", e);
		} catch (Exception e) {
			throw new RepositoryAccessException("Failed to access DB", e);
		}
	}
}
