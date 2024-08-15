package com.mf.api.adapter.out.musicservice.mapper;

import com.mf.api.domain.entity.Playlist;

import java.util.LinkedHashMap;
import java.util.List;

public class SpotifyPlaylistMapper {

	public Playlist map(LinkedHashMap restResponse) {
		return Playlist.builder()
			.id((String) restResponse.get("id"))
			.name((String) restResponse.get("name"))
			.build();
	}

	public List<Playlist> mapList(LinkedHashMap restResponse) {
		var playlists = (List<LinkedHashMap>) restResponse.get("items");
		return playlists.stream()
			.map(this::map)
			.toList();
	}

	public String mapToJson(Playlist playlist) {
		return "{\"name\":\"%s\",\"description\":\"%s\",\"public\":\"%s\"}"
			.formatted(playlist.getName(), "New playlist", "false");
	}
}
