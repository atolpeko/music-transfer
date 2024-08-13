package com.mf.api.adapter.out.musicservice.mapper;

import com.mf.api.domain.entity.Track;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

public class SpotifyTrackMapper  {

	public Track map(LinkedHashMap<Object, Object> restResponse) {
		var data = (LinkedHashMap<Object, Object>) restResponse.get("track");
		if (data == null) {
			data = restResponse;
		}

		return Track.builder()
			.id((String) data.get("id"))
			.name((String) data.get("name"))
			.albumName(extractAlbum(data))
			.artists(extractArtists(data))
			.build();
	}

	private String extractAlbum(LinkedHashMap<Object, Object> data) {
		var album = (LinkedHashMap<Object, Object>) data.get("album");
		return (String) album.get("name");
	}

	private List<String> extractArtists(LinkedHashMap<Object, Object> data) {
		return ((List<LinkedHashMap<Object, Object>>) data.get("artists")).stream()
			.map(artist -> (String) artist.get("name"))
			.toList();
	}

	public List<Track> mapList(LinkedHashMap<Object, Object> restResponse) {
		var container = (LinkedHashMap) restResponse.get("tracks");
		List<LinkedHashMap> tracks;
		if (container != null) {
			tracks = (List<LinkedHashMap>) container.get("items");
		} else {
			tracks = (List<LinkedHashMap>) restResponse.get("items");
		}

		if (tracks == null) {
			return Collections.emptyList();
		}

		return tracks.stream()
			.map(this::map)
			.toList();
	}

	public String idsToJson(Track track) {
		return "{ \"ids\": [ \"%s\" ] }".formatted(track.getId());
	}

	public String idsToJson(List<Track> tracks) {
		var list = tracks.stream()
			.map(track -> "\"%s\"".formatted(track.getId()))
			.reduce((s1, s2) -> String.join(", ", s1, s2))
			.orElseThrow(() -> new RuntimeException("No tracks provided"));

		return "{ \"ids\": [ %s ] }".formatted(list);
	}

	public String urisToJson(List<Track> tracks) {
		var list = tracks.stream()
			.map(track -> "spotify:track:" + track.getId())
			.map("\"%s\""::formatted)
			.reduce((s1, s2) -> String.join(", ", s1, s2))
			.orElseThrow(() -> new RuntimeException("No tracks provided"));

		return "{ \"uris\": [ %s ] }".formatted(list);
	}
}
