package com.mf.auth.adapter.in.rest.controller.musicservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mf.auth.adapter.in.rest.entity.TokenRestEntity;
import com.mf.auth.adapter.in.rest.exception.AuthorizationException;
import com.mf.auth.adapter.in.rest.mapper.TokenMapper;
import com.mf.auth.adapter.in.rest.properties.RestProperties;
import com.mf.auth.adapter.in.rest.service.EncodeStateService;
import com.mf.auth.adapter.in.rest.valueobject.MusicService;
import com.mf.auth.domain.entity.Token;
import com.mf.auth.usecase.AuthUseCase;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.http.HttpStatus;

@Log4j2
@RequiredArgsConstructor
abstract class StandardOauth2AuthController {

	private final MusicService service;
	private final AuthUseCase authUseCase;
	private final EncodeStateService encodeStateService;
	private final RestProperties properties;
	private final TokenMapper tokenMapper;

	public TokenRestEntity callback(String code, String error, String state) {
		try {
			if (error != null || code == null) {
				throw new AuthorizationException(HttpStatus.FORBIDDEN, error);
			}

			var stateData = encodeStateService.decode(state);
			var uuid = authUseCase.decryptWithSecret(
				stateData.getUuid(),
				properties.uuidSecret()
			);

			var token = auth(uuid, stateData.getJwt(), code);
			return tokenMapper.toRestEntity(token);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	private Token auth(String uuid, String jwt, String code) {
		if (jwt == null) {
			return authUseCase.auth(uuid, service.name(), code);
		}

		var decrypted = authUseCase.decryptWithSecret(jwt, uuid);
		return authUseCase.auth(uuid, decrypted, service.name(), code);
	}
}
