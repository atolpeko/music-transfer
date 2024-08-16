package com.mf.api.adapter.out.musicservice.mapper;

import com.mf.api.domain.entity.Playlist;

import java.util.LinkedHashMap;
import java.util.List;

public class YTMusicPlaylistMapper {

	public Playlist map(LinkedHashMap restResponse) {
		var data = (LinkedHashMap) restResponse.get("snippet");
		return Playlist.builder()
			.id((String) restResponse.get("id"))
			.name((String) data.get("title"))
			.build();
	}

	public List<Playlist> mapList(LinkedHashMap restResponse) {
		var playlists = (List<LinkedHashMap>) restResponse.get("items");
		return playlists.stream()
			.map(this::map)
			.toList();
	}
}
