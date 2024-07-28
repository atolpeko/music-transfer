package com.mf.auth.boot.config.beans;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.mf.auth.adapter.in.rest.service.EncodeStateService;
import com.mf.auth.domain.ServiceProperties;
import com.mf.auth.domain.mapper.OAuth2TokenMapper;
import com.mf.auth.domain.service.JWTService;
import com.mf.auth.domain.service.JWTServiceImpl;
import com.mf.auth.domain.service.SymmetricEncryptionService;
import com.mf.auth.domain.service.SymmetricEncryptionServiceImpl;
import com.mf.auth.domain.service.TokenService;
import com.mf.auth.domain.service.TokenServiceIImpl;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceConfig {

	@Bean
	public TokenService tokenService(ServiceProperties properties) {
		return new TokenServiceIImpl(properties);
	}

	@Bean
	public JWTService jwtService(
		ServiceProperties properties,
		OAuth2TokenMapper tokenMapper,
		ObjectMapper objectMapper
	) {
		return new JWTServiceImpl(properties, tokenMapper, objectMapper);
	}

	@Bean
	public OAuth2TokenMapper oAuth2TokenMapper() {
		return new OAuth2TokenMapper();
	}

	@Bean
	public SymmetricEncryptionService encryptionService(ServiceProperties properties) {
		return new SymmetricEncryptionServiceImpl(properties);
	}

	@Bean
	public EncodeStateService encodeStateService(ObjectMapper mapper) {
		return new EncodeStateService(mapper);
	}
}
