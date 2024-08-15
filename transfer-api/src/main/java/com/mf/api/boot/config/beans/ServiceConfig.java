package com.mf.api.boot.config.beans;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.mf.api.domain.service.JWTService;
import com.mf.api.domain.service.impl.JWTDecryptor;
import com.mf.api.domain.service.impl.JWTServiceImpl;
import com.mf.api.domain.service.properties.ServiceProperties;
import com.mf.api.domain.service.mapper.OAuth2TokenMapper;

import io.jsonwebtoken.jackson.io.JacksonDeserializer;

import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceConfig {

	@Bean
	public JWTService jwtService(
		JWTDecryptor decryptor,
		OAuth2TokenMapper tokenMapper,
		ServiceProperties properties
	) {
		return new JWTServiceImpl(decryptor, tokenMapper, properties);
	}

	@Bean
	public JWTDecryptor jwtDecryptor(ObjectMapper mapper) {
		var deserializer = new JacksonDeserializer<Map<String, ?>>(mapper);
		return new JWTDecryptor(deserializer);
	}
}
