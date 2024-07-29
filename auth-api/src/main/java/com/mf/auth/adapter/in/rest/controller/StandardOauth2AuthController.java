package com.mf.auth.adapter.in.rest.controller;

import com.fasterxml.jackson.core.JsonProcessingException;

import com.mf.auth.adapter.in.rest.exception.AuthorizationException;
import com.mf.auth.adapter.in.rest.RestProperties;
import com.mf.auth.adapter.in.rest.service.EncodeStateService;
import com.mf.auth.adapter.in.rest.valueobject.AuthState;
import com.mf.auth.adapter.in.rest.valueobject.MusicService;
import com.mf.auth.adapter.properties.MusicServiceProperties;
import com.mf.auth.domain.entity.Token;
import com.mf.auth.usecase.AuthUseCase;
import com.mf.auth.usecase.JWTUseCase;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
abstract class StandardOauth2AuthController {
	private final MusicService service;
	private final AuthUseCase authUseCase;
	private final JWTUseCase jwtUseCase;
	private final EncodeStateService encodeStateService;
	private final MusicServiceProperties musicProperties;
	private final RestProperties properties;

	public String redirectToAuth(String jwt) throws Exception {
		var uuid = authUseCase.generateUuid().getValue();
		var encryptedJwt = encryptJwt(jwt, uuid);
		var encryptedUuid = authUseCase.encryptWithSecret(uuid, properties.uuidSecret());
		var url = buildAuthUrl(encryptedUuid, encryptedJwt,
			musicProperties.backRedirectUrl());
		return "redirect:" + url;
	}

	private String encryptJwt(String jwt, String uuid) {
		if (jwt == null) {
			return null;
		}

		if (!jwtUseCase.isValid(jwt)) {
			throw new AuthorizationException(
				HttpStatus.UNAUTHORIZED,
				"Provided JWT is invalid"
			);
		}

		return authUseCase.encryptWithSecret(jwt, uuid);
	}

	private String buildAuthUrl(String uuid, String jwt, String redirectUrl)
		throws JsonProcessingException {
		var state = AuthState.builder()
			.uuid(uuid)
			.jwt(jwt)
			.redirectUrl(redirectUrl)
			.build();

		return musicProperties.authUrl()
			+ "?response_type=code"
			+ "&client_id=" + musicProperties.clientId()
			+ "&redirect_uri=" + musicProperties.redirectUrl()
			+ "&scope=" + musicProperties.clientScope()
			+ "&state=" + encodeStateService.encode(state);
	}

	public String callback(
		String code,
		String error,
		String state
	) throws Exception {
		if (error != null || code == null) {
			throw new AuthorizationException(HttpStatus.FORBIDDEN, error);
		}

		var stateData = encodeStateService.decode(state);
		var uuid = authUseCase.decryptWithSecret(stateData.getUuid(),
			properties.uuidSecret());
		var token = auth(uuid, stateData.getJwt(), code).getValue();

		return "redirect:" + stateData.getRedirectUrl() + "?access_token=" + token;
	}

	private Token auth(String uuid, String jwt, String code) {
		if (jwt == null) {
			return authUseCase.auth(uuid, service.name(), code);
		}

		var decrypted = authUseCase.decryptWithSecret(jwt, uuid);
		return authUseCase.auth(uuid, decrypted, service.name(), code);
	}
}
