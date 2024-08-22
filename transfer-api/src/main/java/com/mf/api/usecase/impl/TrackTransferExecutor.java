package com.mf.api.usecase.impl;

import com.mf.api.domain.entity.Track;
import com.mf.api.usecase.entity.TransferResult;
import com.mf.api.usecase.valueobject.TransferContext;

import java.util.Collections;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
public class TrackTransferExecutor {

	private final TrackSearcher trackSearcher;

	public TransferResult<List<Track>> transfer(TransferContext<List<Track>> context) {
		var source = context.getSource();
		var target = context.getTarget();
		var tracks = context.getToTransfer();
		if (tracks.isEmpty()) {
			return TransferResult.of(0, Collections.emptyList());
		}

		log.debug("Starting to transfer {} tracks from {} to {}", tracks.size(), source, target);
		var found = trackSearcher.search(context.getService(), context.getToken(), tracks);
		log.debug("Found {} tracks in {}", found.size(), target);

		context.getService().trackBulkLike(context.getToken(), found);
		log.info("Transferred {} tracks from {} to {}", found.size(), source, target);

		var failed = getFailed(tracks, found);
		if (!failed.isEmpty()) {
			log.info("Failed to transfer {} tracks from {} to {}",
				failed.size(), source, target);
		}

		return TransferResult.of(found.size(), failed);
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
