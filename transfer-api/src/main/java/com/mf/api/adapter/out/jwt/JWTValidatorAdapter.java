package com.mf.api.adapter.out.jwt;

import com.mf.api.port.JWTValidatorPort;
import com.mf.api.util.Param;
import com.mf.api.util.RestRequest;
import com.mf.api.util.RestClient;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.http.HttpMethod;

@Log4j2
@RequiredArgsConstructor
public class JWTValidatorAdapter implements JWTValidatorPort {

	private final RestClient restClient;
	private final JwtValidatorProperties properties;

	@Override
	public boolean isValid(String jwt) {
		try {
			var request = RestRequest.builder()
				.url(properties.jwtValidationUrl())
				.method(HttpMethod.GET)
				.params(List.of(Param.of("jwt", jwt)))
				.build();
			restClient.request(request);
			return true;
		} catch (Exception e) {
			log.debug("JWT validation failed: {}", e.getMessage());
			return false;
		}
	}
}
