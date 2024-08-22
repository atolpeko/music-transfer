package com.mf.api.boot.config.beans;

import com.mf.api.adapter.out.jwt.JWTValidatorAdapter;
import com.mf.api.adapter.out.jwt.JwtValidatorProperties;
import com.mf.api.port.JWTValidatorPort;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class JwtAdapterConfig {

	@Bean
	public JWTValidatorPort jwtServicePort(
		RestTemplate restTemplate,
		JwtValidatorProperties properties
	) {
		return new JWTValidatorAdapter(restTemplate, properties);
	}
}
