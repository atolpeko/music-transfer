package com.mf.auth.adapter.in.rest.controller.musicservice;

import com.mf.auth.adapter.in.rest.properties.RestProperties;
import com.mf.auth.adapter.in.rest.valueobject.MusicService;
import com.mf.auth.usecase.AuthUseCase;
import com.mf.auth.adapter.in.rest.service.EncodeStateService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import springfox.documentation.annotations.ApiIgnore;

@ApiIgnore
@Controller
@RequestMapping(path = "/api/auth/spotify/callback")
public class SpotifyCallbackController extends StandardOauth2AuthController {

	@Autowired
	public SpotifyCallbackController(
		AuthUseCase authUseCase,
		EncodeStateService encodeStateService,
		RestProperties properties
	) {
		super(MusicService.SPOTIFY, authUseCase, encodeStateService, properties);
	}

	@Override
	@GetMapping
	@ResponseStatus(HttpStatus.FOUND)
	public String callback(
		@RequestParam(required = false) String code,
		@RequestParam(required = false) String error,
		@RequestParam String state
	) {
		return super.callback(code, error, state);
	}
}
