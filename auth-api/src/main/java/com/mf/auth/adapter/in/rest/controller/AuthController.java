package com.mf.auth.adapter.in.rest.controller;

import com.fasterxml.jackson.core.JsonProcessingException;

import com.mf.auth.adapter.in.rest.api.AuthAPI;
import com.mf.auth.adapter.in.rest.exception.AuthorizationException;
import com.mf.auth.adapter.in.rest.properties.RestProperties;
import com.mf.auth.adapter.in.rest.service.EncodeStateService;
import com.mf.auth.adapter.in.rest.valueobject.AuthState;
import com.mf.auth.adapter.in.rest.valueobject.MusicService;
import com.mf.auth.adapter.in.rest.valueobject.PropertyMap;
import com.mf.auth.adapter.properties.MusicServiceProperties;
import com.mf.auth.usecase.AuthUseCase;
import com.mf.auth.usecase.JWTUseCase;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;

@Log4j2
@Controller
@RequiredArgsConstructor
public class AuthController implements AuthAPI {

	private final AuthUseCase authUseCase;
	private final JWTUseCase jwtUseCase;
	private final EncodeStateService encodeStateService;
	private final RestProperties properties;
	private final PropertyMap propertyMap;

	@Override
	public String redirectToAuth(MusicService service, String redirectUrl, String jwt) {
		try {
			var uuid = authUseCase.generateUuid().getValue();
			var encryptedJwt = encryptJwt(jwt, uuid);
			var encryptedUuid = authUseCase.encryptWithSecret(uuid, properties.uuidSecret());
			var properties = propertyMap.get(service);
			var url = buildAuthUrl(properties, redirectUrl, encryptedUuid, encryptedJwt);

			log.debug("Redirecting to {} login page", service.name());
			return "redirect:" + url;
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
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

	private String buildAuthUrl(
		MusicServiceProperties properties,
		String redirectUrl,
		String uuid,
		String jwt
	) throws JsonProcessingException {
		var state = AuthState.builder()
			.redirectUrl(redirectUrl)
			.uuid(uuid)
			.jwt(jwt)
			.build();

		return properties.authUrl()
			+ "?response_type=code"
			+ "&client_id=" + properties.clientId()
			+ "&redirect_uri=" + properties.redirectUrl()
			+ "&scope=" + properties.clientScope()
			+ "&state=" + encodeStateService.encode(state);
	}
}
