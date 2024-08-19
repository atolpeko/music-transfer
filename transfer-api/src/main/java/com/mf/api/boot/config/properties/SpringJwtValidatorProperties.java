package com.mf.api.boot.config.properties;

import com.mf.api.adapter.out.jwt.JwtValidatorProperties;

import lombok.Setter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Setter
@Component
public class SpringJwtValidatorProperties implements JwtValidatorProperties {

    @Value("${service.jwtValidator.domain}")
    private String domain;

    @Value("${service.jwtValidator.validationUrl}")
    private String jwtValidationUrl;

    @Override
    public String domain() {
        return domain;
    }

    @Override
    public String jwtValidationUrl() {
        return jwtValidationUrl;
    }
}
