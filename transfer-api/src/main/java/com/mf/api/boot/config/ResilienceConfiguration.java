package com.mf.api.boot.config;

import com.mf.api.boot.config.properties.SpringResilienceProperties;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class ResilienceConfiguration {

	@Bean
	public CircuitBreaker circuitBreaker(SpringResilienceProperties properties) {
		var config = CircuitBreakerConfig.custom()
			.slidingWindowType(CircuitBreakerConfig.SlidingWindowType.TIME_BASED)
			.minimumNumberOfCalls(properties.getMinimumNumberOfCalls())
			.slidingWindowSize(properties.getSlidingWindowSize())
			.failureRateThreshold(properties.getFailureRateThreshold())
			.build();

		var registry = CircuitBreakerRegistry.of(config);
		return registry.circuitBreaker("timeBased");
	}

	@Bean
	public Retry retry() {
		var config = RetryConfig.custom()
			.maxAttempts(3)
			.waitDuration(Duration.ofSeconds(1))
			.build();

		var registry = RetryRegistry.of(config);
		return registry.retry("defaultRetry");
	}
}