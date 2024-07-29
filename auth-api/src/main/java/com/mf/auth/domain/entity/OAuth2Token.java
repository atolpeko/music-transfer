package com.mf.auth.domain.entity;

import com.mf.auth.util.Default;

import java.time.LocalDateTime;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(callSuper = true)
public class OAuth2Token extends Token {
	private final String refreshToken;

	@Default
	public OAuth2Token(String accessToken, String refreshToken, LocalDateTime expiresAt) {
		super(accessToken, expiresAt);
		this.refreshToken = refreshToken;
	}

	public OAuth2Token(String accessToken, String refreshToken, int expiresInSeconds) {
		super(accessToken, expiresInSeconds);
		this.refreshToken = refreshToken;
	}
}
