package com.mf.serviceconfig.boot.config.properties;

import lombok.Getter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class SpringServiceProperties {

    @Value("${service.configLocation}")
    private String configLocation;
}
