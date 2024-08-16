package com.mf.auth.boot.config.beans;

import com.mf.auth.domain.service.JWTService;
import com.mf.auth.domain.service.SymmetricEncryptionService;
import com.mf.auth.domain.service.TokenService;
import com.mf.auth.port.JWTRepositoryPort;
import com.mf.auth.port.UUIDRepositoryPort;
import com.mf.auth.usecase.AuthUseCase;
import com.mf.auth.usecase.impl.AuthUseCaseImpl;
import com.mf.auth.usecase.JWTUseCase;
import com.mf.auth.usecase.impl.JWTUseCaseImpl;
import com.mf.auth.usecase.valueobject.ServiceMap;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public AuthUseCase authUseCase(
        TokenService tokenService,
        JWTService jwtService,
        SymmetricEncryptionService encryptionService,
        UUIDRepositoryPort uuidRepository,
        JWTRepositoryPort jwtRepository,
        CircuitBreaker breaker,
        ServiceMap serviceMap
    ) {
        return new AuthUseCaseImpl(
            tokenService,
            jwtService,
            encryptionService,
            uuidRepository,
            jwtRepository,
            breaker,
            serviceMap
        );
    }

    @Bean
    public JWTUseCase tokenUseCase(
        JWTService jwtService,
        JWTRepositoryPort jwtRepository,
        CircuitBreaker breaker
    ) {
        return new JWTUseCaseImpl(
            jwtService,
            jwtRepository,
            breaker
        );
    }
}
