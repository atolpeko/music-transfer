package com.mf.auth.boot.config.beans;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.mf.auth.adapter.in.rest.service.EncodeStateService;
import com.mf.auth.domain.service.properties.ServiceProperties;
import com.mf.auth.domain.service.impl.JWTDecryptor;
import com.mf.auth.domain.service.impl.JWTGenerator;
import com.mf.auth.domain.service.impl.mapper.OAuth2TokenMapper;
import com.mf.auth.domain.service.JWTService;
import com.mf.auth.domain.service.impl.JWTServiceImpl;
import com.mf.auth.domain.service.SymmetricEncryptionService;
import com.mf.auth.domain.service.impl.SymmetricEncryptionServiceImpl;
import com.mf.auth.domain.service.TokenService;
import com.mf.auth.domain.service.impl.TokenServiceImpl;

import io.jsonwebtoken.jackson.io.JacksonDeserializer;
import io.jsonwebtoken.jackson.io.JacksonSerializer;

import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceConfig {

	@Bean
	public TokenService tokenService(ServiceProperties properties) {
		return new TokenServiceImpl(properties);
	}

	@Bean
	public JWTService jwtService(
		JWTGenerator jwtGenerator,
		JWTDecryptor jwtDecryptor,
		OAuth2TokenMapper tokenMapper,
		ServiceProperties properties
	) {
		return new JWTServiceImpl(jwtGenerator, jwtDecryptor, tokenMapper, properties);
	}

	@Bean
	public JWTGenerator jwtGenerator(ObjectMapper mapper) {
		var serializer = new JacksonSerializer<Map<String, ?>>(mapper);
		return new JWTGenerator(serializer);
	}

	@Bean
	public JWTDecryptor jwtDecryptor(ObjectMapper mapper) {
		var deserializer = new JacksonDeserializer<Map<String, ?>>(mapper);
		return new JWTDecryptor(deserializer);
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
