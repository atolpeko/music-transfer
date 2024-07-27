package com.mf.auth.boot.config.properties;

import com.mf.auth.usecase.properties.UseCaseProperties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SpringUseCaseProperties implements UseCaseProperties {

	@Value("${service.uuidExpirationSeconds}")
	private int uuidExpiration;

	@Value("${service.accessTokenExpirationSeconds}")
	private int accessTokenExpiration;

	@Override
	public int uuidExpirationSeconds() {
		return uuidExpiration;
	}

	@Override
	public int accessTokenExpirationSeconds() {
		return accessTokenExpiration;
	}
}
