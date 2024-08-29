package com.mf.api.boot.config.beans;

import com.mf.api.adapter.out.jwt.JWTValidatorAdapter;
import com.mf.api.adapter.out.jwt.JwtValidatorProperties;
import com.mf.api.port.JWTValidatorPort;

import com.mf.queue.service.RequestQueue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtAdapterConfig {

	@Bean
	public JWTValidatorPort jwtServicePort(
		RequestQueue requestQueue,
		JwtValidatorProperties properties
	) {
		return new JWTValidatorAdapter(requestQueue, properties);
	}
}
