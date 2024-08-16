package com.mf.api.boot.config.beans;

import com.mf.api.domain.service.JWTService;
import com.mf.api.port.JWTValidatorPort;
import com.mf.api.usecase.UseCase;
import com.mf.api.usecase.impl.UseCaseImpl;
import com.mf.api.usecase.impl.PlaylistTransferExecutor;
import com.mf.api.usecase.impl.TrackSearcher;
import com.mf.api.usecase.impl.TrackTransferExecutor;
import com.mf.api.usecase.valueobject.ServiceMap;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public UseCase useCase(
        JWTValidatorPort jwtValidator,
        JWTService jwtService,
        TrackTransferExecutor trackTransferExecutor,
        PlaylistTransferExecutor playlistTransferExecutor,
        ServiceMap serviceMap
    ) {
        return new UseCaseImpl(
            jwtValidator,
            jwtService,
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
