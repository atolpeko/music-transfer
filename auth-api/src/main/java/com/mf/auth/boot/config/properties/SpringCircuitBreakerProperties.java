package com.mf.auth.boot.config.properties;

import lombok.Getter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class SpringCircuitBreakerProperties {

    @Value("${circuitBreaker.minimumNumberOfCalls}")
    private int minimumNumberOfCalls;

    @Value("${circuitBreaker.slidingWindowSize}")
    private int slidingWindowSize;

    @Value("${circuitBreaker.failureRateThreshold}")
    private float failureRateThreshold;
}
