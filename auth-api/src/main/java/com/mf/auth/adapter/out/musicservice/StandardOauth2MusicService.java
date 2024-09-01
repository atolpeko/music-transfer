package com.mf.auth.adapter.out.musicservice;

import com.mf.auth.adapter.properties.MusicServiceProperties;
import com.mf.auth.domain.entity.OAuth2Token;
import com.mf.auth.port.MusicServicePort;
import com.mf.auth.port.exception.AuthException;
import com.mf.auth.port.exception.MusicServiceException;
import com.mf.queue.entity.Request;
import com.mf.queue.service.RequestQueue;

import java.util.Map;
import java.util.Objects;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.HttpClientErrorException;

@RequiredArgsConstructor
public abstract class StandardOauth2MusicService implements MusicServicePort {

	protected final MusicServiceProperties properties;
	private final RequestQueue requestQueue;

	@Override
	public OAuth2Token oauth2ExchangeCode(String authCode) {
		try {
			var request = buildRequest(authCode);
			requestQueue.submit(request);
			var response = request.getResultWhenComplete();
			var body = Objects.requireNonNull(response.getBody());
			return new OAuth2Token(
				(String) body.get("access_token"),
				(String) body.get("refresh_token"),
				(Integer) body.get("expires_in")
			);
		} catch (HttpClientErrorException e) {
			var status = e.getStatusCode().value();
			if (status == 401 || status == 403) {
				var msg = "Authorization failed: %s".formatted(e.getMessage());
				throw new AuthException(msg, e);
			} else {
				var msg = "Service %s unavailable: %s".formatted(properties.name(), e.getMessage());
				throw new MusicServiceException(msg, e);
			}
		} catch (Exception e) {
			var msg = "Service %s unavailable: %s".formatted(properties.name(), e.getMessage());
			throw new MusicServiceException(msg, e);
		}
	}

	private Request<LinkedMultiValueMap<String, String>, Map> buildRequest(String code) {
		var headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		var params = new LinkedMultiValueMap<String, String>();
		params.add("code", code);
		params.add("client_id", properties.clientId());
		params.add("client_secret", properties.clientSecret());
		params.add("grant_type", properties.grantType());
		params.add("redirect_uri", properties.redirectUrl());

		var entity = new HttpEntity<>(params, headers);
		return Request.<LinkedMultiValueMap<String, String>, Map>builder()
			.url(properties.tokenUrl())
			.method(HttpMethod.POST)
			.entity(entity)
			.responseType(Map.class)
			.build();
	}
}
