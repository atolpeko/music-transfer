package com.mf.auth.adapter.in.rest.controller;

import com.mf.auth.adapter.in.rest.api.TokenAPI;
import com.mf.auth.adapter.in.rest.entity.TokenRestEntity;
import com.mf.auth.adapter.in.rest.mapper.TokenMapper;
import com.mf.auth.usecase.JWTUseCase;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TokenController implements TokenAPI {

	private final JWTUseCase useCase;
	private final TokenMapper tokenMapper;

	@Override
	public TokenRestEntity getJwt(String accessToken) {
		var jwt = useCase.obtain(accessToken);
		return tokenMapper.toRestEntity(jwt);
	}

	@Override
	public ResponseEntity<String> validateJwt(String jwt) {
		return useCase.isValid(jwt)
			? ResponseEntity.ok("valid")
			: ResponseEntity.status(401).body("not valid");
	}
}
