package com.mf.auth.domain.entity;

import com.mf.auth.util.Default;

import java.time.LocalDateTime;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class AccessToken extends Token {
	private String id;
	private boolean isUsed;

	@Default
	public AccessToken(String id, String value, LocalDateTime expiresAt, boolean isUsed) {
		super(value, expiresAt);
		this.id = id;
		this.isUsed = isUsed;
	}

	public AccessToken(String value, int expiresInSeconds) {
		super(value, expiresInSeconds);
		this.isUsed = false;
	}
}
