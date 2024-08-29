package com.mf.queue.entity;

import com.mf.queue.exception.InvalidUrlException;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Request to execute. Use getResultWhenComplete() to access result when it's ready.
 *
 * @param <T> request entity type
 * @param <K> response entity type
 */
@Builder
@ToString
@EqualsAndHashCode
public class Request<T, K> {

	@Getter
	private String url;

	@Getter
	private HttpMethod method;

	@Getter
	private HttpEntity<T> entity;

	@Getter
	private Class<K> responseType;

	@Builder.Default
	private final CompletableFuture<ResponseEntity<K>> result = new CompletableFuture<>();

	/**
	 * Get request's host.
	 *
	 * @return host
	 *
	 * @throws InvalidUrlException if request URL is invalid
	 */
	public String getHost() {
		try {
			return new URL(url).getHost();
		} catch (MalformedURLException e) {
			throw new InvalidUrlException(url, e.getMessage(), e);
		}
	}

	/**
	 * Execute this request and fill response future.
	 *
	 * @param restTemplate request executor
	 */
	public void execute(RestTemplate restTemplate) {
		try {
			var response = restTemplate.exchange(url, method, entity, responseType);
			result.complete(response);
		} catch (Exception e) {
			result.completeExceptionally(e);
		}
	}

	/**
	 * Returns the result when complete.
	 *
	 * @return result
	 */
	public ResponseEntity<K> getResultWhenComplete() {
		try {
			return result.join();
		} catch (CompletionException wrapped) {
			var cause = wrapped.getCause();
			if (cause instanceof HttpClientErrorException e) {
				throw e;
			} else if (cause instanceof HttpMessageConversionException e) {
				throw e;
			} else if (cause instanceof InvalidMediaTypeException e) {
				throw e;
			} else if (cause instanceof IllegalStateException e) {
				throw e;
			} else if (cause instanceof RestClientException e) {
				throw e;
			} else {
				throw wrapped;
			}
		}
	}
}