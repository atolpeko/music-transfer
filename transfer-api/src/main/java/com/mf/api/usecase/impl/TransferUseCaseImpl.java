package com.mf.api.usecase.impl;

import com.mf.api.domain.entity.OAuth2Token;
import com.mf.api.domain.entity.Track;
import com.mf.api.port.MusicServicePort;
import com.mf.api.port.exception.AccessException;
import com.mf.api.usecase.TransferUseCase;
import com.mf.api.usecase.exception.AuthorizationException;
import com.mf.api.usecase.exception.InvalidRequestException;
import com.mf.api.usecase.exception.UseCaseException;
import com.mf.api.usecase.entity.TransferResult;
import com.mf.api.usecase.valueobject.ServiceMap;
import com.mf.api.usecase.entity.TransferRequest;
import com.mf.api.usecase.valueobject.TokenMap;
import com.mf.api.usecase.valueobject.TransferContext;
import com.mf.api.util.type.Tuple;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
public class TransferUseCaseImpl implements TransferUseCase {

    private final TrackTransferExecutor trackTransferExecutor;
    private final PlaylistTransferExecutor playlistTransferExecutor;
    private final ServiceMap serviceMap;

    @Override
    public TransferResult transfer(TransferRequest request) {
        try {
            validateParams(request.getSource(), request.getTarget());
            var context = TransferContext.builder()
                .source(request.getSource())
                .target(request.getTarget())
                .sourceSvc(getService(request.getSource()))
                .targetSvc(getService(request.getTarget()))
                .sourceToken(getToken(request.getTokenMap(), request.getSource()))
                .targetToken(getToken(request.getTokenMap(), request.getTarget()))
                .build();

            var tracksResult = trackTransferExecutor.transfer(context);
            var playlistResult = Tuple.of(0, Collections.<Track>emptyList());
            if (request.isTransferPlaylists()) {
                playlistResult = playlistTransferExecutor.transfer(context);
            }

            var failed = new HashSet<>(tracksResult.getSecond());
            failed.addAll(playlistResult.getSecond());

            return TransferResult.builder()
                .transferredTracks(tracksResult.getFirst())
                .transferredPlaylists(playlistResult.getFirst())
                .failedToTransfer(new ArrayList<>(failed))
                .build();
        } catch (UseCaseException e) {
            throw e;
        } catch (AccessException e) {
            throw new AuthorizationException(e.getMessage(), e);
        } catch (UnsupportedOperationException e) {
            throw new InvalidRequestException("Operation is not supported");
        } catch (Exception e) {
            throw new UseCaseException("Service unavailable", e);
        }
    }

    private void validateParams(String source, String target) {
        if (source.equals(target)) {
            throw new InvalidRequestException("Source should be different from target");
        }
    }

    private MusicServicePort getService(String service) {
       return Optional.ofNullable(serviceMap.get(service))
            .orElseThrow(() -> new InvalidRequestException(service + " is not supported"));
    }

    private OAuth2Token getToken(TokenMap tokenMap, String service) {
        return Optional.of(tokenMap.get(service))
            .orElseThrow(() -> new AuthorizationException("No auth token for " + service));
    }
}
