package com.mf.api.adapter.out.jwt;

import com.mf.api.port.JWTValidatorPort;
import com.mf.api.port.exception.RestException;
import com.mf.queue.entity.Request;
import com.mf.queue.service.RequestQueue;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.http.HttpMethod;
import org.springframework.web.client.HttpClientErrorException;

@Log4j2
@RequiredArgsConstructor
public class JWTValidatorAdapter implements JWTValidatorPort {

	private final RequestQueue requestQueue;
	private final JwtValidatorProperties properties;

	@Override
	public boolean isValid(String jwt) {
		try {
			var request = buildRequest(jwt);
			requestQueue.submit(request);
			var response = request.getResultWhenComplete();
			return response.getStatusCode().is2xxSuccessful();
		} catch (HttpClientErrorException e) {
			var status = e.getStatusCode();
			if (status.is4xxClientError()) {
				return false;
			} else {
				var msg = "Service unavailable: %s".formatted(e.getMessage());
				throw new RestException(msg, e);
			}
		} catch (Exception e) {
			var msg = "Service unavailable: %s".formatted(e.getMessage());
			throw new RestException(msg, e);
		}
	}

	private Request<String, Void> buildRequest(String jwt) {
		return Request.<String, Void>builder()
			.url(properties.domain() + properties.jwtValidationUrl() + "?jwt=" + jwt)
			.method(HttpMethod.GET)
			.responseType(Void.class)
			.build();
	}
}
