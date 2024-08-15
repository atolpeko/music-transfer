package com.mf.api.boot.config.properties;

import com.mf.api.domain.service.properties.ServiceProperties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SpringServiceProperties implements ServiceProperties {

	@Value("${service.jwtSecret}")
	private String jwtSecret;

	@Value("${service.tokenPrefix}")
	private String tokenPrefix;

	@Override
	public String jwtSecret() {
		return jwtSecret;
	}

	@Override
	public String tokenPrefix() {
		return tokenPrefix;
	}
}
