package com.mf.api.adapter.out.musicservice.mapper;

import com.mf.api.domain.entity.Track;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class YTMusicTrackMapper {

	public Track map(LinkedHashMap restResponse) {
		var data = (LinkedHashMap) restResponse.get("snippet");
		var desc = (String) data.get("description");
		if (!desc.startsWith("Provided to YouTube by")) {
			// Not a YT Music track
			return null;
		}

		var title = (String) data.get("title");
		var	artist = extractArtist(data);
		return Track.builder()
			.serviceId((String) restResponse.get("id"))
			.name((String) data.get("title"))
			.albumName(extractAlbum(desc, artist, title))
			.artists(List.of(artist))
			.build();
	}

	private String extractArtist(LinkedHashMap data) {
		return Optional.ofNullable((String) data.get("videoOwnerChannelTitle"))
			.orElse((String) data.get("channelTitle"))
			.replace(" - Topic", "");
	}

	private String extractAlbum(
		String desc,
		String artist,
		String title
	) {
		try {
			var titleArtist = title + " · " + artist + "\n\n";
			var artistEnd = desc.substring(desc.indexOf(titleArtist) + titleArtist.length());
			return artistEnd.substring(0, artistEnd.indexOf("\n"));
		} catch (Exception e) {
			return "";
		}
	}

	public List<Track> mapList(LinkedHashMap restResponse) {
		var tracks = (List<LinkedHashMap>) restResponse.get("items");
		if (tracks == null || tracks.isEmpty()) {
			return Collections.emptyList();
		}

		return tracks.stream()
			.map(this::map)
			.filter(Objects::nonNull)
			.toList();
	}
}
