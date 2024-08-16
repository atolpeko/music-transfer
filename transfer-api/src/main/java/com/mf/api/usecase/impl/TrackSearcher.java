package com.mf.api.usecase.impl;

import com.mf.api.domain.entity.OAuth2Token;
import com.mf.api.domain.entity.Track;
import com.mf.api.domain.valueobject.TrackSearchCriteria;
import com.mf.api.port.MusicServicePort;
import com.mf.api.util.type.Tuple;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

public class TrackSearcher {

	/**
	 * Search for a list of tracks.
	 *
	 * @param service  music service to search in
	 * @param token    auth token
	 * @param tracks   a list of tracks to search for
	 *
	 * @return         found tracks
	 */
	public List<Track> search(
		MusicServicePort service,
		OAuth2Token token,
		List<Track> tracks
	) {
		return IntStream.range(0, tracks.size())
			.mapToObj(i -> Tuple.of(i, tracks.get(i)))
			.parallel()
			.map(track -> findTrack(service, token, track))
			.sorted(Comparator.comparing(Tuple::getFirst))
			.map(Tuple::getSecond)
			.filter(Objects::nonNull)
			.toList();
	}

	private Tuple<Integer, Track> findTrack(
		MusicServicePort service,
		OAuth2Token token,
		Tuple<Integer, Track> track
	) {
		var name = (track.getSecond().getName() != null)
			? track.getSecond().getName()
			: "";
		var album = (track.getSecond().getAlbumName() != null)
			? track.getSecond().getAlbumName()
			: "";
		List<String> artists = (track.getSecond().getArtists() != null)
			? track.getSecond().getArtists()
			: Collections.emptyList();

		var fullSearch = TrackSearchCriteria.builder()
			.trackName(name)
			.albumName(album)
			.artists(artists)
			.build();
		var byNameAndArtist = TrackSearchCriteria.builder()
			.trackName(name)
			.artists(artists)
			.build();
		var byName = TrackSearchCriteria.builder()
			.trackName(name)
			.build();

		var found = service.searchTracks(token, fullSearch)
			.or(() -> service.searchTracks(token, byNameAndArtist))
			.or(() -> service.searchTracks(token, byName));
		found.ifPresent(t -> t.setUniqueId(track.getSecond().getUniqueId()));

		return Tuple.of(track.getFirst(), found.orElse(null));
	}
}
