package com.mf.api.adapter.out.musicservice.mapper;

import com.mf.api.domain.entity.Track;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

public class YTMusicTrackMapper {

	public Track map(LinkedHashMap<Object, Object> restResponse) {
		var data = (LinkedHashMap<Object, Object>) restResponse.get("snippet");
		var title = (String) data.get("title");
		var	artist = extractArtist(data);
		return Track.builder()
			.id((String) restResponse.get("id"))
			.name((String) data.get("title"))
			.albumName(extractAlbum(data, artist, title))
			.artists(List.of(artist))
			.build();
	}

	private String extractArtist(LinkedHashMap<Object, Object> data) {
		return Optional.ofNullable((String) data.get("videoOwnerChannelTitle"))
			.orElse((String) data.get("channelTitle"))
			.replace(" - Topic", "");
	}

	private String extractAlbum(
		LinkedHashMap<Object, Object> data,
		String artist,
		String title
	) {
		try {
			var desc = (String) data.get("description");
			var titleArtist = title + " · " + artist + "\n\n";
			var artistEnd = desc.substring(desc.indexOf(titleArtist) + titleArtist.length());
			return artistEnd.substring(0, artistEnd.indexOf("\n"));
		} catch (Exception e) {
			return "";
		}
	}

	public List<Track> mapList(LinkedHashMap<Object, Object> restResponse) {
		var tracks = (List<LinkedHashMap<Object, Object>>) restResponse.get("items");
		return tracks.stream()
			.map(this::map)
			.toList();
	}
}
