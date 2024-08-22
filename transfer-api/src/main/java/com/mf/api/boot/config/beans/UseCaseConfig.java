package com.mf.api.boot.config.beans;

import com.mf.api.domain.service.JWTService;
import com.mf.api.port.JWTValidatorPort;
import com.mf.api.usecase.AuthUseCase;
import com.mf.api.usecase.TransferUseCase;
import com.mf.api.usecase.impl.AuthUseCaseImpl;
import com.mf.api.usecase.impl.TransferUseCaseImpl;
import com.mf.api.usecase.impl.PlaylistTransferExecutor;
import com.mf.api.usecase.impl.TrackSearcher;
import com.mf.api.usecase.impl.TrackTransferExecutor;
import com.mf.api.usecase.valueobject.ServiceMap;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public AuthUseCase authUseCase(
        JWTValidatorPort jwtValidator,
        JWTService jwtService
    ) {
        return new AuthUseCaseImpl(jwtValidator, jwtService);
    }

    @Bean
    public TransferUseCase useCase(
        TrackTransferExecutor trackTransferExecutor,
        PlaylistTransferExecutor playlistTransferExecutor,
        ServiceMap serviceMap
    ) {
        return new TransferUseCaseImpl(
            trackTransferExecutor,
            playlistTransferExecutor,
            serviceMap
        );
    }

    @Bean
    public TrackSearcher trackSearcher() {
        return new TrackSearcher();
    }

    @Bean
    public TrackTransferExecutor trackTransferExecutor(
        TrackSearcher trackSearcher
    ) {
        return new TrackTransferExecutor(trackSearcher);
    }

    @Bean
    public PlaylistTransferExecutor playlistTransferExecutor(
        TrackSearcher trackSearcher
    ) {
        return new PlaylistTransferExecutor(trackSearcher);
    }
}
