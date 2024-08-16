package com.mf.api.boot.config.properties;

import lombok.Getter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class SpringResilienceProperties {

    @Value("${circuitBreaker.minimumNumberOfCalls}")
    private int minimumNumberOfCalls;

    @Value("${circuitBreaker.slidingWindowSize}")
    private int slidingWindowSize;

    @Value("${circuitBreaker.failureRateThreshold}")
    private float failureRateThreshold;

    @Value("${retry.attempts}")
    private int attempts;

    @Value("${retry.windowSeconds}")
    private int windowSeconds;
}
