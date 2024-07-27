package com.mf.auth.boot.config.properties;

import com.mf.auth.domain.ServiceProperties;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SpringServiceProperties implements ServiceProperties {

	@Value("${service.jwtIssuer}")
	private String jwtIssuer;

	@Value("${service.jwtSecret}")
	private String jwtSecret;

	@Value("${service.jwtExpirationSeconds}")
	private int jwtExpiration;

	@Value("${service.encryptionAlgName}")
	private String encryptionAlgName;

	@Value("${service.encryptionKeySize}")
	private int encryptionKeySize;

	@Value("${service.encryptionTransformation}")
	private String encryptionTransformation;

	@PostConstruct
	public void assertSecretLength() {
		if (jwtSecret.length() < 35) {
			var msg = "JWT secret is too short. Consider using at least 35 characters";
			throw new RuntimeException(msg);
		}
	}

	@Override
	public String jwtIssuer() {
		return jwtIssuer;
	}

	@Override
	public String jwtSecret() {
		return jwtSecret;
	}

	@Override
	public int jwtExpirationSeconds() {
		return jwtExpiration;
	}

	@Override
	public String encryptionAlgName() {
		return encryptionAlgName;
	}

	@Override
	public int encryptionKeySize() {
		return encryptionKeySize;
	}

	@Override
	public String encryptionTransformation() {
		return encryptionTransformation;
	}
}
