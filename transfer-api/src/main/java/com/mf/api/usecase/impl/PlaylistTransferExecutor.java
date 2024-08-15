package com.mf.api.usecase.impl;

import com.mf.api.domain.entity.Playlist;
import com.mf.api.domain.entity.Track;
import com.mf.api.usecase.valueobject.TransferContext;
import com.mf.api.util.type.Tuple;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
public class PlaylistTransferExecutor {

	private final TrackSearcher trackSearcher;

	public Tuple<Integer, List<Track>> transfer(TransferContext context) {
		var source = context.getSource();
		var target = context.getTarget();
		log.debug("Starting to transfer playlists from {} to {}", source, target);

		var playlists = context.getSourceSvc().playlists(context.getSourceToken());
		log.debug("Fetched {} playlists from {}", playlists.size(), source);
		if (playlists.isEmpty()) {
			return Tuple.of(0, Collections.emptyList());
		}

		var failed = playlists.stream()
			.map(playlist -> transferPlaylist(playlist, context))
			.parallel()
			.map(res -> getFailed(res.getFirst(), res.getSecond()))
			.flatMap(List::stream)
			.collect(Collectors.toSet());

		log.info("Transferred {} playlists from {} to {}", playlists.size(), source, target);
		if (!failed.isEmpty()) {
			log.debug("Failed to add {} tracks from {} to playlists in {}",
				failed.size(), source, target);
		}

		return Tuple.of(playlists.size(), new ArrayList<>(failed));
	}

	private Tuple<List<Track>, List<Track>> transferPlaylist(
		Playlist playlist,
		TransferContext context
	) {
		playlist.setName(playlist.getName() + "_" + UUID.randomUUID());
		var id = context.getTargetSvc().createPlaylist(
			context.getTargetToken(),
			playlist
		);
		log.debug("Created playlist {} in {}", playlist.getName(), context.getTarget());

		var foundTracks = trackSearcher.search(
			context.getTargetSvc(),
			context.getTargetToken(),
			playlist.getTracks()
		);
		context.getTargetSvc().addToPlaylist(context.getTargetToken(), id, foundTracks);

		return Tuple.of(playlist.getTracks(), foundTracks);
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
