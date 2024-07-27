package com.mf.auth.domain;

public interface ServiceProperties {

	String jwtIssuer();
	String jwtSecret();
	int jwtExpirationSeconds();
	String encryptionAlgName();
	int encryptionKeySize();
	String encryptionTransformation();
}
