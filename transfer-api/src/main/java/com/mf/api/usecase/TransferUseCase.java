package com.mf.api.usecase;

import com.mf.api.domain.entity.Playlist;
import com.mf.api.domain.entity.Track;
import com.mf.api.usecase.exception.AuthorizationException;
import com.mf.api.usecase.exception.UseCaseException;
import com.mf.api.usecase.exception.InvalidRequestException;
import com.mf.api.usecase.entity.TransferResult;
import com.mf.api.usecase.entity.TransferRequest;
import com.mf.api.usecase.valueobject.TokenMap;
import com.mf.api.util.Page;

import java.util.List;

public interface TransferUseCase {

    /**
     * Transfer tracks to the specified target music service.
     *
     * @param request  request object holding transfer data
     *
     * @return result object containing transfer result data
     *
     * @throws InvalidRequestException  if target is not permitted
     * @throws AuthorizationException   if fails to authorize into a music service
     * @throws UseCaseException         in case of any other error
     */
    TransferResult<List<Track>> transferTracks(TransferRequest<List<Track>> request);

    /**
     * Transfer playlists to the specified target music service.
     *
     * @param request  request object holding transfer data
     *
     * @return result object containing transfer result data
     *
     * @throws InvalidRequestException  if target is not permitted
     * @throws AuthorizationException   if fails to authorize into a music service
     * @throws UseCaseException         in case of any other error
     */
    TransferResult<Playlist> transferPlaylist(TransferRequest<Playlist> request);

    /**
     * Get a page of tracks available for transfer.
     *
     * @param service   music service
     * @param tokenMap  authorization token map
     * @param next      next page identifier
     *
     * @return a page of tracks available for transfer.
     *
     * @throws AuthorizationException  if fails to authorize into a music service
     * @throws UseCaseException        in case of any other error
     */
    Page<Track> findTracks(String service, TokenMap tokenMap, String next);

    /**
     * Get a page of playlist tracks available for transfer.
     *
     * @param service     music service
     * @param tokenMap    authorization token map
     * @param playlistId  playlist ID
     * @param next        next page identifier
     *
     * @return a page of playlist tracks available for transfer.
     *
     * @throws AuthorizationException  if fails to authorize into a music service
     * @throws UseCaseException        in case of any other error
     */
    Page<Track> findPlaylistTracks(
        String service,
        TokenMap tokenMap,
        String playlistId,
        String next
    );

    /**
     * Get a page of playlists available for transfer.
     *
     * @param service   music service
     * @param tokenMap  authorization token map
     * @param next      next page identifier
     *
     * @return a page of playlists available for transfer.
     *
     * @throws AuthorizationException  if fails to authorize into a music service
     * @throws UseCaseException        in case of any other error
     */
    Page<Playlist> findPlaylists(String service, TokenMap tokenMap, String next);
}
