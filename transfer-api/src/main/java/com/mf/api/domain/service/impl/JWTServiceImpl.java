package com.mf.api.domain.service.impl;

import com.mf.api.domain.entity.OAuth2Token;
import com.mf.api.domain.service.JWTService;
import com.mf.api.domain.service.properties.ServiceProperties;
import com.mf.api.domain.service.mapper.OAuth2TokenMapper;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JWTServiceImpl implements JWTService {

	private final JWTDecryptor decryptor;
	private final OAuth2TokenMapper tokenMapper;
	private final ServiceProperties properties;

	@Override
	public Map<String, OAuth2Token> extractTokens(String jwt) {
		var claims = decryptor.decrypt(properties.jwtSecret(), jwt);
		var prefix = properties.tokenPrefix();
		return claims.entrySet().stream()
			.filter(record -> record.getKey().startsWith(prefix))
			.map(record -> Map.entry(
				record.getKey().replace(prefix, ""),
				tokenMapper.map((LinkedHashMap<String, Object>) record.getValue())))
			.collect(Collectors.toMap(Entry::getKey, Entry::getValue));
	}
}
