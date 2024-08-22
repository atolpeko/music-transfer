package com.mf.api.adapter.in.rest.controller;

import com.mf.api.adapter.in.rest.api.TransferAPI;
import com.mf.api.adapter.in.rest.entity.PlaylistRestEntity;
import com.mf.api.adapter.in.rest.entity.TrackRestEntity;
import com.mf.api.adapter.in.rest.entity.TracksRestEntity;
import com.mf.api.adapter.in.rest.entity.TransferResultRestEntity;
import com.mf.api.adapter.in.rest.mapper.PlaylistMapper;
import com.mf.api.adapter.in.rest.mapper.TrackMapper;
import com.mf.api.adapter.in.rest.entity.MusicService;
import com.mf.api.domain.entity.Playlist;
import com.mf.api.domain.entity.Track;
import com.mf.api.usecase.AuthUseCase;
import com.mf.api.usecase.TransferUseCase;
import com.mf.api.usecase.entity.TransferRequest;
import com.mf.api.util.Page;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
public class TransferController implements TransferAPI {

	private final AuthUseCase authUseCase;
	private final TransferUseCase transferUseCase;
	private final TrackMapper trackMapper;
	private final PlaylistMapper playlistMapper;

	@Override
	public Page<TrackRestEntity> availableTracks(
		MusicService service,
		String authToken,
		String next
	) {
		var tokenMap = authUseCase.extractTokens(authToken);
		var result = transferUseCase.findTracks(service.name(), tokenMap, next);
		var tracks = mapTracksToRest(result.getItems());
		return Page.of(tracks, result.getNext());
	}

	private List<TrackRestEntity> mapTracksToRest(List<Track> tracks) {
		return tracks.stream()
			.map(trackMapper::toRestEntity)
			.toList();
	}

	@Override
	public TransferResultRestEntity<List<TrackRestEntity>> transferTracks(
		MusicService source,
		MusicService target,
		TracksRestEntity tracks,
		String authToken
	) {
		var tokenMap = authUseCase.extractTokens(authToken);
		var request = TransferRequest.<List<Track>>builder()
			.source(source.name())
			.target(target.name())
			.tokenMap(tokenMap)
			.toTransfer(mapTracks(tracks.getTracks()))
			.build();

		var result = transferUseCase.transferTracks(request);
		return TransferResultRestEntity.<List<TrackRestEntity>>builder()
			.transferred(result.getTransferredCount())
			.failedToTransfer(mapTracksToRest(result.getFailed()))
			.build();
	}

	private List<Track> mapTracks(List<TrackRestEntity> tracks) {
		return tracks.stream()
			.map(trackMapper::toEntity)
			.toList();
	}

	@Override
	public Page<PlaylistRestEntity> availablePlaylists(
		MusicService service,
		String authToken,
		String next
	) {
		var tokenMap = authUseCase.extractTokens(authToken);
		var result = transferUseCase.findPlaylists(service.name(), tokenMap, next);
		var playlists = mapPlaylistsToRest(result.getItems());
		return Page.of(playlists, result.getNext());
	}

	private List<PlaylistRestEntity> mapPlaylistsToRest(List<Playlist> playlists) {
		return playlists.stream()
			.map(playlistMapper::toRestEntity)
			.toList();
	}

	@Override
	public Page<TrackRestEntity> availablePlaylistTracks(
		MusicService service,
		String authToken,
		String playlistId,
		String next
	) {
		var tokenMap = authUseCase.extractTokens(authToken);
		var result = transferUseCase.findPlaylistTracks(
			service.name(),
			tokenMap,
			playlistId,
			next
		);

		var tracks = mapTracksToRest(result.getItems());
		return Page.of(tracks, result.getNext());
	}

	@Override
	public TransferResultRestEntity<PlaylistRestEntity> transferPlaylist(
		MusicService source,
		MusicService target,
		PlaylistRestEntity playlist,
		String authToken
	) {
		var tokenMap = authUseCase.extractTokens(authToken);
		var request = TransferRequest.<Playlist>builder()
			.source(source.name())
			.target(target.name())
			.tokenMap(tokenMap)
			.toTransfer(playlistMapper.toEntity(playlist))
			.build();

		var result = transferUseCase.transferPlaylist(request);
		return TransferResultRestEntity.<PlaylistRestEntity>builder()
			.transferred(result.getTransferredCount())
			.failedToTransfer(playlistMapper.toRestEntity(result.getFailed()))
			.build();
	}
}
