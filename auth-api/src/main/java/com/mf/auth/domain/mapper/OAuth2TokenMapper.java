package com.mf.auth.domain.mapper;

import com.mf.auth.domain.entity.OAuth2Token;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;

import java.util.List;

public class OAuth2TokenMapper {

	public OAuth2Token map(LinkedHashMap<String, Object> map) {
		return new OAuth2Token(
			(String) map.get("value"),
			(String) map.get("refreshToken"),
			mapExpiration((List<Integer>) map.get("expiresAt"))
		);
	}

	private LocalDateTime mapExpiration(List<Integer> data) {
		var date = LocalDate.of(data.get(0), data.get(1), data.get(2));
		var time = LocalTime.of(data.get(3), data.get(4), data.get(5), data.get(6));
		return LocalDateTime.of(date, time);
	}
}
