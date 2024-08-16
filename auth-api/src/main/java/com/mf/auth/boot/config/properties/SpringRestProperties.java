package com.mf.auth.boot.config.properties;

import com.mf.auth.adapter.in.rest.properties.RestProperties;

import lombok.Getter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SpringRestProperties implements RestProperties {

    @Value("${service.uuidSecret}")
    private String uuidSecret;

    @Value("${swagger.url}")
    private String swaggerUrl;

    @Getter
    @Value("${swagger.title}")
    private String title;

    @Getter
    @Value("${swagger.description}")
    private String description;

    @Getter
    @Value("${swagger.version}")
    private String version;

    @Getter
    @Value("${swagger.controllersPackage}")
    private String controllersPackage;

    @Override
    public String swaggerUrl() {
        return swaggerUrl;
    }

    @Override
    public String uuidSecret() {
        return uuidSecret;
    }
}
