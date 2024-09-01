package com.mf.api.adapter.out.musicservice;

import com.mf.api.adapter.out.musicservice.properties.DefaultMusicServiceProperties;
import com.mf.api.domain.entity.OAuth2Token;
import com.mf.api.port.MusicServicePort;
import com.mf.api.port.exception.AccessException;
import com.mf.api.port.exception.IllegalRequestException;
import com.mf.api.port.exception.MusicServiceException;
import com.mf.api.util.Page;
import com.mf.queue.entity.Request;
import com.mf.queue.service.RequestQueue;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

@Log4j2
@RequiredArgsConstructor
public abstract class BaseMusicServiceAdapter implements MusicServicePort {

	private final RequestQueue requestQueue;
	private final DefaultMusicServiceProperties properties;

	protected String getUrl(String url) {
		return properties.domain() + url;
	}

	protected <T> Page<T> getForList(
		String url,
		OAuth2Token token,
		Function<LinkedHashMap, String> paginationMapper,
		Function<LinkedHashMap, List<T>> itemMapper
	) {
		try {
			var response = request(url, HttpMethod.GET, token, null, LinkedHashMap.class, true);
			var body = Objects.requireNonNull(response.getBody());
			var nextPage = paginationMapper.apply(body);
			var items = itemMapper.apply(body);
			return Page.of(items, nextPage);
		} catch (MusicServiceException e) {
			log.error(e.getMessage());
			throw e;
		} catch (NullPointerException e) {
			return Page.of(Collections.emptyList());
		}
	}

	protected <T> ResponseEntity<T> request(
		String url,
		HttpMethod method,
		OAuth2Token token,
		String json,
		Class<T> responseType,
		boolean retry
	) {
		try {
			var request = (json != null)
				? buildRequest(url, method, token, json, responseType, retry)
				: buildRequest(url, method, token, responseType, retry);
			requestQueue.submit(request);
			return request.getResultWhenComplete();
		} catch (HttpClientErrorException e) {
			var status = e.getStatusCode();
			if (status.value() == 401 || status.value() == 403) {
				throw new AccessException("Authorization failed");
			} else if (status.value() == 429) {
				var retrySecs = Integer.parseInt(e.getResponseHeaders().get("Retry-After").get(0));
				var msg = "%s is unavailable for %s minutes"
					.formatted(properties.name(), retrySecs / 60);
				throw new MusicServiceException(msg, e);
			} else if (status.is4xxClientError()){
				var msg = "Invalid request to %s: %s".formatted(url, e.getMessage());
				throw new IllegalRequestException(msg);
			} else {
				var msg = "Service %s unavailable: %s".formatted(url, e.getMessage());
				throw new MusicServiceException(msg);
			}
		} catch (IllegalArgumentException e) {
			throw new IllegalRequestException(e.getMessage(), e);
		} catch (Exception e) {
			throw new MusicServiceException(e.getMessage(), e.getCause());
		}
	}

	private <T> Request<?, T> buildRequest(
		String url,
		HttpMethod method,
		OAuth2Token token,
		Class<T> responseType,
		boolean retry
	) {
		var headers = new HttpHeaders();
		headers.setBearerAuth(token.getValue());

		return Request.<Object, T>builder()
			.url(url)
			.method(method)
			.entity(new HttpEntity<>(headers))
			.responseType(responseType)
			.retryIfFails(retry)
			.retryTimes(2)
			.timeoutSeconds(5 * 1000)
			.build();
	}

	private <T> Request<?, T> buildRequest(
		String url,
		HttpMethod method,
		OAuth2Token token,
		String json,
		Class<T> responseType,
		boolean retry
	) {
		var headers = new HttpHeaders();
		headers.setBearerAuth(token.getValue());
		headers.setContentType(MediaType.APPLICATION_JSON);

		return Request.<Object, T>builder()
			.url(url)
			.method(method)
			.entity(new HttpEntity<>(json, headers))
			.responseType(responseType)
			.retryIfFails(retry)
			.retryTimes(2)
			.timeoutSeconds(5 * 1000)
			.build();
	}
}
