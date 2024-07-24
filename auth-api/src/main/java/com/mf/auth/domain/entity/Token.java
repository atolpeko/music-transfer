package com.mf.auth.domain.entity;

import com.mf.auth.util.Default;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class Token {
	private final String value;
	private final LocalDateTime expiresAt;

	@Default
	public Token(String value, LocalDateTime expiresAt) {
		this.value = value;
		this.expiresAt = expiresAt;
	}

	public Token(String value, int expiresInSeconds) {
		this.value = value;
		this.expiresAt = LocalDateTime.now().plusSeconds(expiresInSeconds);
	}

	public boolean isValid() {
		return LocalDateTime.now().isBefore(expiresAt);
	}
}
