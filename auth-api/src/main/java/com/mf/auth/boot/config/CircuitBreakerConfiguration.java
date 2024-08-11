package com.mf.auth.boot.config;

import com.mf.auth.boot.config.properties.SpringCircuitBreakerProperties;
import com.mf.auth.port.exception.MusicServiceException;
import com.mf.auth.port.exception.RepositoryException;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.SlidingWindowType;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CircuitBreakerConfiguration {

	@Bean
	public CircuitBreaker circuitBreaker(SpringCircuitBreakerProperties properties) {
		var config = CircuitBreakerConfig.custom()
			.slidingWindowType(SlidingWindowType.TIME_BASED)
			.ignoreExceptions(MusicServiceException.class, RepositoryException.class)
			.minimumNumberOfCalls(properties.getMinimumNumberOfCalls())
			.slidingWindowSize(properties.getSlidingWindowSize())
			.failureRateThreshold(properties.getFailureRateThreshold())
			.build();

		var registry = CircuitBreakerRegistry.of(config);
		return registry.circuitBreaker("timeBased");
	}

}
