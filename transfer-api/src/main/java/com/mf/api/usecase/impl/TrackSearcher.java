package com.mf.api.usecase.impl;

import com.mf.api.domain.entity.OAuth2Token;
import com.mf.api.domain.entity.Track;
import com.mf.api.domain.valueobject.TrackSearchCriteria;
import com.mf.api.port.MusicServicePort;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
		return tracks.stream()
			.map(track -> findTrack(service, token, track))
			.filter(Optional::isPresent)
			.map(Optional::get)
			.toList();
	}

	private Optional<Track> findTrack(
		MusicServicePort service,
		OAuth2Token token,
		Track track
	) {
		var name = (track.getName() != null)
			? track.getName()
			: "";
		var album = (track.getAlbumName() != null)
			? track.getAlbumName()
			: "";
		List<String> artists = (track.getArtists() != null)
			? track.getArtists()
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
		found.ifPresent(t -> t.setUniqueId(track.getUniqueId()));
		return found;
	}
}
