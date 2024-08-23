package com.mf.auth.adapter.in.rest.controller.musicservice;

import com.mf.auth.adapter.in.rest.entity.TokenRestEntity;
import com.mf.auth.adapter.in.rest.mapper.TokenMapper;
import com.mf.auth.adapter.in.rest.properties.RestProperties;
import com.mf.auth.adapter.in.rest.service.EncodeStateService;
import com.mf.auth.adapter.in.rest.valueobject.MusicService;
import com.mf.auth.usecase.AuthUseCase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import springfox.documentation.annotations.ApiIgnore;

@ApiIgnore
@RestController
@RequestMapping(path = "/api/auth/ytmusic/callback")
public class YTMusicCallbackController extends StandardOauth2AuthController {

	@Autowired
	public YTMusicCallbackController(
		AuthUseCase authUseCase,
		EncodeStateService encodeStateService,
		RestProperties properties,
		TokenMapper mapper
	) {
		super(MusicService.YT_MUSIC, authUseCase, encodeStateService, properties, mapper);
	}

	@Override
	@GetMapping
	public TokenRestEntity callback(
		@RequestParam(required = false) String code,
		@RequestParam(required = false) String error,
		@RequestParam String state
	) {
		return super.callback(code, error, state);
	}
}
