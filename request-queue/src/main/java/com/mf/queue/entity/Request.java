package com.mf.queue.entity;

import com.mf.queue.exception.InvalidUrlException;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

/**
 * Request to execute. Use getResultWhenComplete() to access result when it's ready.
 *
 * @param <T> request entity type
 * @param <K> response entity type
 */
@Log4j2
@ToString
@EqualsAndHashCode
public class Request<T, K> {

	/**
	 * Default request timeout.
	 */
	public static final int DEFAULT_TIMEOUT_MILLIS = 3 * 1000;

	/**
	 * Default retry waiting timeout.
	 */
	public static final int DEFAULT_RETRY_WAIT_MILLIS = 2 * 1000;

	@Getter
	private final String url;

	@Getter
	private final HttpMethod method;

	@Getter
	private final HttpEntity<T> entity;

	@Getter
	private final Class<K> responseType;

	@Getter
	private final int timeoutMillis;

	@Getter
	private final boolean retryIfFails;

	@Getter
	private final int retryTimes;

	@Getter
	private final int retryWaitMillis;

	@Getter
	private final long startTimeMillis;

	private final AtomicInteger retryCount;
	private final CompletableFuture<ResponseEntity<K>> result;

	@Builder
	public Request(
		String url,
		HttpMethod method,
		HttpEntity<T> entity,
		Class<K> responseType,
		int timeoutMillis,
		boolean retryIfFails,
		int retryTimes,
		int retryWaitMillis
	) {
		this.url = url;
		this.method = method;
		this.entity = entity;
		this.responseType = responseType;
		this.retryIfFails = retryIfFails;
		this.retryTimes = retryTimes;
		this.retryWaitMillis = (retryWaitMillis == 0)
			? DEFAULT_RETRY_WAIT_MILLIS
			: retryWaitMillis;
		this.timeoutMillis = (timeoutMillis == 0)
			? DEFAULT_TIMEOUT_MILLIS
			: timeoutMillis;

		result = new CompletableFuture<>();
		retryCount = new AtomicInteger(0);
		startTimeMillis = System.currentTimeMillis();
	}

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
			var msg = "Invalid request URL: " + url;
			throw new InvalidUrlException(this, msg, e);
		}
	}

	/**
	 * Returns the result when complete.
	 *
	 * @return completed result
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

	/**
	 * Complete this request.
	 *
	 * @param response  response
	 */
	public void complete(ResponseEntity<K> response) {
		result.complete(response);
	}

	/**
	 * Complete this request with exception.
	 *
	 * @param e  exception
	 */
	public void fail(Exception e) {
		result.completeExceptionally(e);
	}

	/**
	 * Return true if this request is still actual or false otherwise.
	 *
	 * @return true if this request is still actual or false otherwise.
	 */
	public boolean actual() {
		return startTimeMillis + timeoutMillis > System.currentTimeMillis();
	}

	/**
	 * Get this request retry count.
	 *
	 * @return retry count
	 */
	public int retriedTimes() {
		return retryCount.get();
	}

	/**
	 * Increment this request count.
	 */
	public void retried() {
		retryCount.incrementAndGet();
	}
}