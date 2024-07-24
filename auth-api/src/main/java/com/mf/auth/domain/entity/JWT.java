package com.mf.auth.domain.entity;

import com.mf.auth.util.Default;

import java.time.LocalDateTime;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class JWT extends Token {
	private String id;
	private Token accessToken;

	public JWT(String value, int expiresInSeconds) {
		super(value, expiresInSeconds);
	}

	public JWT(String value, LocalDateTime expiresAt, Token accessToken) {
		super(value, expiresAt);
		this.accessToken = accessToken;
	}

	public JWT(String value, int expiresInSeconds, Token accessToken) {
		super(value, expiresInSeconds);
		this.accessToken = accessToken;
	}

	@Default
	public JWT(String id, String value, LocalDateTime expiresAt, Token accessToken) {
		super(value, expiresAt);
		this.id = id;
		this.accessToken = accessToken;
	}
}
