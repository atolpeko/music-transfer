package com.mf.api.boot.config.properties;

import lombok.Getter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class SpringResilienceProperties {

    @Value("${retry.attempts}")
    private int attempts;

    @Value("${retry.windowSeconds}")
    private int windowSeconds;
}
