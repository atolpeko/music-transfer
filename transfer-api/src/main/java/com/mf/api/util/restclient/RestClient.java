package com.mf.api.util.restclient;

import com.mf.api.usecase.exception.AuthorizationException;
import com.mf.api.util.restclient.exception.ClientErrorException;
import com.mf.api.util.restclient.exception.ServerErrorException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.retry.Retry;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Function;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@Log4j2
@RequiredArgsConstructor
public class RestClient {

	private final RestTemplate restTemplate;
	private final CircuitBreaker breaker;
	private final Retry retry;

	public <T> PageableRestResponse<T> requestPage(
		RestRequest request,
		Function<LinkedHashMap<Object, Object>, List<T>> mapper
	) {
		try {
			validate(request, true);
			request.setLimit(request.getLimit() + 1);

			var response = call(request, LinkedHashMap.class);
			var body = (LinkedHashMap<Object, Object>) response.getBody();
			var items = mapper.apply(body);

			var hasNext = items.size() == request.getLimit();
			items = (items.size() == request.getLimit())
				? items.subList(0, items.size() - 1)
				: items;
			return new PageableRestResponse<>(items, request.getOffset(), hasNext);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private <T> ResponseEntity<T> call(
		RestRequest request,
		Class<T> clazz
	) throws Exception {
		var url = getUrl(request);
		var entity = getHttpEntity(request);
		log.debug(
			"Executing HTTP {} to {} with {} headers and {} body{}",
			request.getMethod(),
			url,
			entity.getHeaders().size(),
			(request.getJson() == null) ? "no" : "json",
			(request.isRetryIfFails()) ? ". Will retry if fails" : ""
		);

		Callable<ResponseEntity<T>> req = () -> breaker.executeCallable(
			() -> restTemplate.exchange(url, request.getMethod(), entity, clazz)
		);

		return (request.isRetryIfFails())
			? checkResponse(retry.executeCallable(req))
			: checkResponse(req.call());
	}

	private String getUrl(RestRequest request) {
		var builder = new StringBuilder(request.getUrl());

		var params = request.getParams().stream()
			.map(Param::toString)
			.reduce((s1, s2) -> String.join("&", s1, s2));
		params.ifPresent(p -> builder.append("?").append(p));

		if (request.getOffset() == null && request.getLimit() == null) {
			return builder.toString();
		}

		builder.append((params.isPresent()) ? "&" : "?");
		return builder.append("offset").append("=").append(request.getOffset())
			.append("&")
			.append("limit").append("=").append(request.getLimit()).toString();
	}

	private void validate(RestRequest request, boolean paginationRequired) {
		if (request.getUrl() == null || request.getMethod() == null) {
			var msg = "Invalid request: specify URL and method";
			throw new RuntimeException(msg);
		} else if (paginationRequired
			&& (request.getOffset() == null || request.getLimit() == null)) {
			var msg = "Invalid request: set offset and limit";
			throw new RuntimeException(msg);
		} else if ((request.getLimit() != null && request.getOffset() == null)
			|| (request.getOffset() != null && request.getLimit() == null)) {
			var msg = "Invalid request: specify both offset and limit";
			throw new RuntimeException(msg);
		}
	}

	private HttpEntity getHttpEntity(RestRequest request) {
		var headers = new HttpHeaders();
		if (request.getJson() != null) {
			headers.setContentType(MediaType.APPLICATION_JSON);
		}
		if (request.getToken() != null) {
			headers.setBearerAuth(request.getToken().getValue());
		}

		return request.getJson() != null
			? new HttpEntity<>(request.getJson(), headers)
			: new HttpEntity<>(headers);
	}

	private <T> ResponseEntity<T> checkResponse(ResponseEntity<T> response) {
		var status = response.getStatusCode();
		if (status.is2xxSuccessful() || status.is3xxRedirection()) {
			return response;
		} else if (status.value() == 401 || status.value() == 403) {
			throw new AuthorizationException();
		} else if (status.is4xxClientError()) {
			throw new ClientErrorException();
		} else {
			throw new ServerErrorException();
		}
	}

	public <T> T request(
		RestRequest request,
		Function<LinkedHashMap<Object, Object>, T> mapper
	) {
		try {
			validate(request, false);
			var response =  call(request, LinkedHashMap.class);
			var body = (LinkedHashMap<Object, Object>) response.getBody();
			return mapper.apply(body);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void request(RestRequest request) {
		try {
			validate(request, false);
			call(request, Void.class);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
