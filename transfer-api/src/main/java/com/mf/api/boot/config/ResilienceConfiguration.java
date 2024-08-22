package com.mf.api.boot.config;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class ResilienceConfiguration {

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