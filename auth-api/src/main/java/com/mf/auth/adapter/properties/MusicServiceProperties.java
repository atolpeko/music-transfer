package com.mf.auth.adapter.properties;

public interface MusicServiceProperties {

	String clientId();
	String clientSecret();
	String clientScope();
	String grantType();
	String authUrl();
	String tokenUrl();
	String redirectUrl();
	String backRedirectUrl();
}
