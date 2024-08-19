package com.mf.api.adapter.out.musicservice;

import com.mf.api.adapter.out.musicservice.properties.DefaultMusicServiceProperties;
import com.mf.api.domain.entity.OAuth2Token;
import com.mf.api.port.MusicServicePort;
import com.mf.api.port.exception.AccessException;
import com.mf.api.port.exception.MusicServiceException;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.retry.Retry;

import java.util.concurrent.Callable;

import lombok.RequiredArgsConstructor;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@Log4j2
@RequiredArgsConstructor
public abstract class BaseMusicServiceAdapter implements MusicServicePort {

	private final RestTemplate restTemplate;
	private final CircuitBreaker circuitBreaker;
	private final Retry retry;
	private final DefaultMusicServiceProperties properties;

	protected String getUrl(String url) {
		return properties.domain() + url;
	}

	protected <T> ResponseEntity<T> execRequest(
		String url,
		HttpMethod method,
		OAuth2Token token,
		Class<T> clazz
	) throws Exception {
		var callable = callable(url, method, token, null, clazz);
		var response = retry.executeCallable(
			() -> circuitBreaker.executeCallable(callable)
		);

		return checkResponse(url, response);
	}

	private <T> Callable<ResponseEntity<T>> callable(
		String url,
		HttpMethod method,
		OAuth2Token token,
		String json,
		Class<T> clazz
	) {
		var headers = new HttpHeaders();
		headers.setBearerAuth(token.getValue());
		if (json != null) {
			headers.setContentType(MediaType.APPLICATION_JSON);
		}
		var entity = (json != null)
			? new HttpEntity<>(json, headers)
			: new HttpEntity<>(headers);

		log.debug("Executing HTTP {} to {}", method.name(), url);
		return () -> restTemplate.exchange(url, method, entity, clazz);
	}

	private <T> ResponseEntity<T> checkResponse(String url, ResponseEntity<T> response) {
		var status = response.getStatusCode();
		if (status.is2xxSuccessful()) {
			return response;
		} else if (status.value() == 401 || status.value() == 403) {
			throw new AccessException("Authorization failed");
		} else {
			var msg = "Failed to request %s: %s".formatted(url, status);
			throw new MusicServiceException(msg);
		}
	}

	protected <T> ResponseEntity<T> execRequest(
		String url,
		HttpMethod method,
		OAuth2Token token,
		String json,
		Class<T> clazz
	) throws Exception {
		var callable = callable(url, method, token, json, clazz);
		var response = retry.executeCallable(
			() -> circuitBreaker.executeCallable(callable)
		);

		return checkResponse(url, response);
	}
}
