package com.mf.auth.boot.config;

import com.mf.auth.boot.config.properties.CircuitBreakerProperties;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CircuitBreakerConfiguration {

	@Bean
	public CircuitBreaker circuitBreaker(CircuitBreakerProperties properties) {
		var config = CircuitBreakerConfig.custom()
			.slidingWindowType(CircuitBreakerConfig.SlidingWindowType.TIME_BASED)
			.minimumNumberOfCalls(properties.getMinimumNumberOfCalls())
			.slidingWindowSize(properties.getSlidingWindowSize())
			.failureRateThreshold(properties.getFailureRateThreshold())
			.build();

		var registry = CircuitBreakerRegistry.of(config);
		return registry.circuitBreaker("timeBased");
	}

}
