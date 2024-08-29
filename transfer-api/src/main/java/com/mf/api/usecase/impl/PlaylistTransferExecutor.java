package com.mf.api.usecase.impl;

import com.mf.api.domain.entity.Playlist;
import com.mf.api.domain.entity.Track;
import com.mf.api.usecase.entity.TransferResult;
import com.mf.api.usecase.valueobject.TransferContext;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
public class PlaylistTransferExecutor {

	private final TrackSearcher trackSearcher;

	public TransferResult<Playlist> transfer(TransferContext<Playlist> context) {
		var source = context.getSource();
		var target = context.getTarget();
		var playlist = context.getToTransfer();

		log.debug("Starting to transfer {} playlist from {} to {}",
			playlist.getName(), source, target);
		playlist.setName(playlist.getName() + "_" + UUID.randomUUID());
		var id = context.getService().createPlaylist(context.getToken(), playlist);
		log.debug("Created playlist {} in {}", playlist.getName(), context.getTarget());

		var foundTracks = trackSearcher.search(
			context.getService(),
			context.getToken(),
			playlist.getTracks()
		);
		if (!foundTracks.isEmpty()) {
			context.getService().addToPlaylist(context.getToken(), id, foundTracks);
			log.debug("Transferred {} tracks for playlist {}",
				foundTracks.size(), context.getToTransfer().getName());
		}

		log.info("Transferred playlist {} from {} to {}",
			context.getToTransfer().getName(), source, target);

		var failedTracks = getFailed(playlist.getTracks(), foundTracks);
		if (failedTracks.isEmpty()) {
			return TransferResult.of(1, null);
		}

		playlist.setTracks(failedTracks);
		return TransferResult.of(1, playlist);
	}

	private List<Track> getFailed(List<Track> tracks, List<Track> found) {
		if (tracks.size() == found.size()) {
			return Collections.emptyList();
		}

		return tracks.parallelStream()
			.filter(track -> !found.contains(track))
			.toList();
	}
}
