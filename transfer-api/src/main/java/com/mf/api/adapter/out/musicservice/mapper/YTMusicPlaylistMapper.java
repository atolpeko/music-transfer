package com.mf.api.adapter.out.musicservice.mapper;

import com.mf.api.domain.entity.Playlist;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

public class YTMusicPlaylistMapper {

	public Playlist map(LinkedHashMap restResponse) {
		var data = (LinkedHashMap) restResponse.get("snippet");
		return Playlist.builder()
			.serviceId((String) restResponse.get("id"))
			.name((String) data.get("title"))
			.imgUrl(extractImageUrl(data))
			.build();
	}

	private String extractImageUrl(LinkedHashMap data) {
		try {
			var thumbnails = (LinkedHashMap) Objects.requireNonNull(data.get("thumbnails"));
			var image = (LinkedHashMap) Objects.requireNonNull(thumbnails.get("high"));
			return (String) image.get("url");
		} catch (NullPointerException e) {
			return null;
		}
	}

	public List<Playlist> mapList(LinkedHashMap restResponse) {
		var playlists = (List<LinkedHashMap>) restResponse.get("items");
		return playlists.stream()
			.map(this::map)
			.toList();
	}
}
