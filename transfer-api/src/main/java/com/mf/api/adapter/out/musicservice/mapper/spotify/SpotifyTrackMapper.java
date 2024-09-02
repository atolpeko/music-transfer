package com.mf.api.adapter.out.musicservice.mapper.spotify;

import com.mf.api.domain.entity.Track;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

public class SpotifyTrackMapper  {

	public Track map(LinkedHashMap restResponse) {
		var data = (LinkedHashMap) restResponse.get("track");
		if (data == null) {
			data = restResponse;
		}

		var album = (LinkedHashMap) data.get("album");
		return Track.builder()
			.serviceId((String) data.get("id"))
			.name((String) data.get("name"))
			.imgUrl(extractImageUrl(album))
			.albumName((String) album.get("name"))
			.artists(extractArtists(data))
			.build();
	}

	private String extractImageUrl(LinkedHashMap data) {
		var images = (List) data.get("images");
		var image = (LinkedHashMap) images.get(0);
		return (String) image.get("url");
	}

	private List<String> extractArtists(LinkedHashMap data) {
		return ((List<LinkedHashMap>) data.get("artists")).stream()
			.map(artist -> (String) artist.get("name"))
			.toList();
	}

	public List<Track> mapList(LinkedHashMap restResponse) {
		var container = (LinkedHashMap) restResponse.get("tracks");
		List<LinkedHashMap> tracks;
		if (container != null) {
			tracks = (List<LinkedHashMap>) container.get("items");
		} else {
			tracks = (List<LinkedHashMap>) restResponse.get("items");
		}

		if (tracks == null || tracks.isEmpty()) {
			return Collections.emptyList();
		}

		return tracks.stream()
			.map(this::map)
			.toList();
	}

	public String idsToJson(Track track) {
		return "{ \"ids\": [ \"%s\" ] }".formatted(track.getServiceId());
	}

	public String idsToJson(List<Track> tracks) {
		var list = tracks.stream()
			.map(track -> "\"%s\"".formatted(track.getServiceId()))
			.reduce((s1, s2) -> String.join(", ", s1, s2))
			.orElseThrow(() -> new RuntimeException("No tracks provided"));

		return "{ \"ids\": [ %s ] }".formatted(list);
	}

	public String urisToJson(List<Track> tracks) {
		var list = tracks.stream()
			.map(track -> "spotify:track:" + track.getServiceId())
			.map("\"%s\""::formatted)
			.reduce((s1, s2) -> String.join(", ", s1, s2))
			.orElseThrow(() -> new RuntimeException("No tracks provided"));

		return "{ \"uris\": [ %s ] }".formatted(list);
	}
}
