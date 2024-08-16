package com.mf.api.usecase.impl;

import com.mf.api.domain.entity.Track;
import com.mf.api.usecase.valueobject.TransferContext;
import com.mf.api.util.type.Tuple;

import java.util.Collections;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
public class TrackTransferExecutor {

	private final TrackSearcher trackSearcher;

	public Tuple<Integer, List<Track>> transfer(TransferContext context) {
		var source = context.getSource();
		var target = context.getTarget();
		log.debug("Starting to transfer tracks from {} to {}", source, target);

		var tracks = context.getSourceSvc().likedTracks(context.getSourceToken());
		if (tracks.isEmpty()) {
			return Tuple.of(0, Collections.emptyList());
		}
		log.debug("Fetched {} tracks from {}", tracks.size(), source);

		var found = trackSearcher.search(context.getTargetSvc(), context.getTargetToken(), tracks);
		log.debug("Found {} tracks in {}", found.size(), target);

		context.getTargetSvc().trackBulkLike(context.getTargetToken(), found);
		log.info("Transferred {} tracks from {} to {}", found.size(), source, target);

		var failed = getFailed(tracks, found);
		if (!failed.isEmpty()) {
			log.info("Failed to transfer {} tracks from {} to {}",
				failed.size(), source, target);
		}

		return Tuple.of(found.size(), failed);
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
