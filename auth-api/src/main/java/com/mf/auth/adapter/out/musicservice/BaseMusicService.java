package com.mf.auth.adapter.out.musicservice;

import com.mf.auth.adapter.properties.MusicServiceProperties;
import com.mf.auth.domain.entity.OAuth2Token;
import com.mf.auth.port.MusicServicePort;
import com.mf.auth.port.exception.MusicServiceException;

import java.util.Map;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
public abstract class BaseMusicService implements MusicServicePort {
	private final MusicServiceProperties properties;
	private final RestTemplate restTemplate;

	@Override
	public OAuth2Token oauth2ExchangeCode(String authCode) {
		try {
			var headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

			var params = new LinkedMultiValueMap<String, String>();
			params.add("code", authCode);
			params.add("client_id", properties.clientId());
			params.add("client_secret", properties.clientSecret());
			params.add("grant_type", properties.grantType());
			params.add("redirect_uri", properties.redirectUrl());

			var url = properties.tokenUrl();
			var request = new HttpEntity<>(params, headers);
			var response = restTemplate.exchange(url, HttpMethod.POST, request, Map.class);

			var body = response.getBody();
			return new OAuth2Token(
				(String) body.get("access_token"),
				(String) body.get("refresh_token"),
				(Integer) body.get("expires_in")
			);
		} catch (Exception e) {
			var msg = "Authorization failed: " + e.getMessage();
			throw new MusicServiceException(msg, e);
		}
	}
}
