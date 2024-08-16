package com.mf.auth.adapter.in.rest.controller;

import com.mf.auth.adapter.in.rest.api.SpotifyAuthAPI;
import com.mf.auth.adapter.in.rest.RestProperties;
import com.mf.auth.adapter.in.rest.valueobject.MusicService;
import com.mf.auth.adapter.properties.MusicServiceProperties;
import com.mf.auth.usecase.AuthUseCase;
import com.mf.auth.adapter.in.rest.service.EncodeStateService;
import com.mf.auth.usecase.JWTUseCase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;

@Controller
public class SpotifyAuthController
	extends StandardOauth2AuthController
	implements SpotifyAuthAPI {

	@Autowired
	public SpotifyAuthController(
		AuthUseCase authUseCase,
		JWTUseCase jwtUseCase,
		EncodeStateService encodeStateService,
		@Qualifier("spotifyProperties") MusicServiceProperties musicServiceProperties,
		RestProperties restProperties
	) {
		super(MusicService.SPOTIFY, authUseCase, jwtUseCase,
			encodeStateService, musicServiceProperties, restProperties);
	}

	@Override
	public String redirectToAuth(String redirectUrl, String jwt) throws Exception {
		return super.redirectToAuth(redirectUrl, jwt);
	}

	@Override
	public String callback(
		String code,
		String error,
		String state
	) throws Exception {
		return super.callback(code, error, state);
	}
}
