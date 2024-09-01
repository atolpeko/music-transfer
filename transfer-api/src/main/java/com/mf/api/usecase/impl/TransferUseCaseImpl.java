package com.mf.api.usecase.impl;

import com.mf.api.domain.entity.OAuth2Token;
import com.mf.api.domain.entity.Playlist;
import com.mf.api.domain.entity.Track;
import com.mf.api.port.MusicServicePort;
import com.mf.api.port.exception.AccessException;
import com.mf.api.port.exception.IllegalRequestException;
import com.mf.api.port.exception.MusicServiceException;
import com.mf.api.usecase.TransferUseCase;
import com.mf.api.usecase.exception.AuthorizationException;
import com.mf.api.usecase.exception.InvalidRequestException;
import com.mf.api.usecase.exception.UseCaseException;
import com.mf.api.usecase.entity.TransferResult;
import com.mf.api.usecase.valueobject.ServiceMap;
import com.mf.api.usecase.entity.TransferRequest;
import com.mf.api.usecase.valueobject.TokenMap;
import com.mf.api.usecase.valueobject.TransferContext;
import com.mf.api.util.Page;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
public class TransferUseCaseImpl implements TransferUseCase {

    private final TrackTransferExecutor trackTransferExecutor;
    private final PlaylistTransferExecutor playlistTransferExecutor;
    private final ServiceMap serviceMap;

    @Override
    public TransferResult<List<Track>> transferTracks(TransferRequest<List<Track>> request) {
        Callable<TransferResult<List<Track>>> callable = () -> {
            var context = TransferContext.<List<Track>>builder()
                .source(request.getSource())
                .target(request.getTarget())
                .toTransfer(request.getToTransfer())
                .service(getService(request.getTarget()))
                .token(getToken(request.getTokenMap(), request.getTarget()))
                .build();
            return trackTransferExecutor.transfer(context);
        };

        return withExceptionHandling(callable);
    }

    private MusicServicePort getService(String service) {
        return Optional.ofNullable(serviceMap.get(service))
            .orElseThrow(() -> new InvalidRequestException(service + " is not supported"));
    }

    private OAuth2Token getToken(TokenMap tokenMap, String service) {
        return Optional.ofNullable(tokenMap.get(service))
            .orElseThrow(() -> new AuthorizationException("No auth token for " + service));
    }

    private <T> T withExceptionHandling(Callable<T> callable) {
        try {
            return callable.call();
        } catch (UseCaseException e) {
            throw e;
        } catch (AccessException e) {
            throw new AuthorizationException(e.getMessage(), e);
        } catch (IllegalRequestException e) {
            throw new InvalidRequestException(e.getMessage(), e);
        } catch (UnsupportedOperationException e) {
            throw new InvalidRequestException("Operation is not supported");
        } catch (MusicServiceException e) {
            throw new UseCaseException(e.getMessage(), e);
        } catch (Exception e) {
            throw new UseCaseException("Service unavailable", e);
        }
    }

    @Override
    public TransferResult<Playlist> transferPlaylist(TransferRequest<Playlist> request) {
       Callable<TransferResult<Playlist>> callable = () -> {
            var context = TransferContext.<Playlist>builder()
                .source(request.getSource())
                .target(request.getTarget())
                .toTransfer(request.getToTransfer())
                .service(getService(request.getTarget()))
                .token(getToken(request.getTokenMap(), request.getTarget()))
                .build();

            return playlistTransferExecutor.transfer(context);
        };

       return withExceptionHandling(callable);
    }

    @Override
    public Page<Track> findTracks(
        String service,
        TokenMap tokenMap,
        String next
    ) {
        Callable<Page<Track>> callable = () -> {
            var svc = getService(service);
            var token = getToken(tokenMap, service);
            return svc.likedTracks(token, next);
        };

        return withExceptionHandling(callable);
    }

    @Override
    public Page<Playlist> findPlaylists(
        String service,
        TokenMap tokenMap,
        String next
    ) {
        Callable<Page<Playlist>> callable = () -> {
            var svc = getService(service);
            var token = getToken(tokenMap, service);
            return svc.playlists(token, next);
        };

        return withExceptionHandling(callable);
    }

    @Override
    public Page<Track> findPlaylistTracks(
        String service,
        TokenMap tokenMap,
        String playlistId,
        String next
    ) {
        Callable<Page<Track>> callable = () -> {
            var svc = getService(service);
            var token = getToken(tokenMap, service);
            return svc.playlistTracks(token, playlistId, next);
        };

        return withExceptionHandling(callable);
    }
}
