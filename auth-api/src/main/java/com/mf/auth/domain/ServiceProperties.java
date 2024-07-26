package com.mf.auth.domain;

public interface ServiceProperties {

	String jwtIssuer();
	String jwtSecret();
	int jwtExpirationSeconds();
	String encryptionAlgName();
	String encryptionTransformation();
}
