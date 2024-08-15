package com.mf.api.adapter.out.jwt;

import com.mf.api.port.JWTValidatorPort;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.web.client.RestTemplate;

@Log4j2
@RequiredArgsConstructor
public class JWTValidatorAdapter implements JWTValidatorPort {

	private final RestTemplate restTemplate;
	private final CircuitBreaker breaker;
	private final JwtValidatorProperties properties;

	@Override
	public boolean isValid(String jwt) {
		try {
			var url = properties.jwtValidationUrl() + "?jwt=" + jwt;
			log.debug("Executing HTTP GET to {}", url);
			var response = breaker.executeCallable(
				() -> restTemplate.getForEntity(url, Void.class)
			);

			return response.getStatusCode().is2xxSuccessful();
		} catch (Exception e) {
			return false;
		}
	}
}
