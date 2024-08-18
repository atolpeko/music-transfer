package com.mf.auth.domain.service.properties;

public interface ServiceProperties {

	String jwtIssuer();

	String jwtSecret();

	int jwtExpirationSeconds();

	String encryptionAlgName();

	int encryptionKeySize();

	String encryptionTransformation();

	int uuidExpirationSeconds();

	int accessTokenExpirationSeconds();
}
