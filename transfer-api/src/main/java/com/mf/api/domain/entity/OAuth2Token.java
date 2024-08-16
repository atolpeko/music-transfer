package com.mf.api.domain.entity;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class OAuth2Token {

	private String value;
	private String refreshToken;
	private LocalDateTime expiresAt;

	public boolean isValid() {
		return LocalDateTime.now().isBefore(expiresAt);
	}
}
