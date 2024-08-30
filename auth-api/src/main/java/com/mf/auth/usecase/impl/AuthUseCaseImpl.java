package com.mf.auth.usecase.impl;

import com.mf.auth.domain.entity.AccessToken;
import com.mf.auth.domain.entity.Token;
import com.mf.auth.domain.service.JWTService;
import com.mf.auth.domain.service.SymmetricEncryptionService;
import com.mf.auth.domain.service.TokenService;
import com.mf.auth.domain.service.exception.InvalidKeyException;
import com.mf.auth.port.JWTRepositoryPort;
import com.mf.auth.port.UUIDRepositoryPort;
import com.mf.auth.port.exception.AuthException;
import com.mf.auth.port.exception.DataAccessException;
import com.mf.auth.port.exception.DataModificationException;
import com.mf.auth.usecase.AuthUseCase;
import com.mf.auth.usecase.exception.AuthorizationException;
import com.mf.auth.usecase.exception.RepositoryAccessException;
import com.mf.auth.usecase.exception.UseCaseException;
import com.mf.auth.usecase.valueobject.ServiceMap;
import com.mf.auth.domain.entity.OAuth2Token;

import java.util.Map;

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

	private final ServiceMap serviceMap;

	@Override
	public Token generateUuid() {
		try {
			var uuid = tokenService.generateUuid();
			uuidRepository.save(uuid);
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
		try {
			return encryptionService.decrypt(toDecrypt, secret);
		} catch (InvalidKeyException e) {
			throw new AuthorizationException(e.getMessage(), e);
		}
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

			jwtRepository.save(jwt);
			log.info("Authenticated into {}", service);
			return accessToken;
		} catch (UseCaseException e) {
			throw e;
		} catch (DataAccessException | DataModificationException e) {
			var msg = "Failed to access DB: %s".formatted(e.getMessage());
			throw new RepositoryAccessException(msg, e);
		} catch (Exception e) {
			var msg = "Service unavailable: %s".formatted(e.getMessage());
			throw new UseCaseException(msg, e);
		}
	}

	private Token findValidUuid(String uuidValue) {
		var uuid = uuidRepository.findValidByValue(uuidValue);
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
		} catch (AuthException e) {
			throw new AuthorizationException(e.getMessage(), e);
		} catch (Exception e) {
			throw new UseCaseException(e.getMessage(), e);
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
			var oldJwt = jwtRepository.findValidByValue(jwt)
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

			jwtRepository.deleteById(oldJwt.getId());
			jwtRepository.save(newJwt);
			log.info("Authenticated into {}", service);
			return accessToken;
		} catch (UseCaseException e) {
			throw e;
		} catch (DataModificationException e) {
			var msg = "Failed to access DB: %s".formatted(e.getMessage());
			throw new RepositoryAccessException(msg, e);
		} catch (Exception e) {
			var msg = "Service unavailable: %s".formatted(e.getMessage());
			throw new UseCaseException(msg, e);
		}
	}
}
