package com.mf.api.domain.entity;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OAuth2Token {
	private String value;
	private String refreshToken;
	private LocalDateTime expiresAt;
}
