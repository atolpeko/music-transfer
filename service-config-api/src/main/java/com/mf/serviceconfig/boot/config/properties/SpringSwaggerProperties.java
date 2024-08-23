package com.mf.serviceconfig.boot.config.properties;

import com.mf.serviceconfig.rest.properties.SwaggerProperties;

import lombok.Getter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SpringSwaggerProperties implements SwaggerProperties {

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
}
