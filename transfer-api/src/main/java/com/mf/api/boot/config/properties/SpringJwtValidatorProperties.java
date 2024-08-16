package com.mf.api.boot.config.properties;

import com.mf.api.adapter.out.jwt.JwtValidatorProperties;

import lombok.Getter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class SpringJwtValidatorProperties implements JwtValidatorProperties {

    @Value("${service.jwtValidationUrl}")
    private String jwtValidationUrl;

    @Override
    public String jwtValidationUrl() {
        return jwtValidationUrl;
    }
}
