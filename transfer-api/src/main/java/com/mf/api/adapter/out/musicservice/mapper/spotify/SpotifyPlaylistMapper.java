package com.mf.api.adapter.out.musicservice.mapper.spotify;

import com.mf.api.domain.entity.Playlist;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

public class SpotifyPlaylistMapper {

	public Playlist map(LinkedHashMap restResponse) {
		return Playlist.builder()
			.serviceId((String) restResponse.get("id"))
			.name((String) restResponse.get("name"))
			.imgUrl(extractImageUrl(restResponse))
			.build();
	}

	private String extractImageUrl(LinkedHashMap data) {
		try {
			var images = (List) Objects.requireNonNull(data.get("images"));
			if (images.isEmpty()) {
				return  null;
			}

			var image = (LinkedHashMap) Objects.requireNonNull(images.get(0));
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

	public String mapToJson(Playlist playlist) {
		return "{\"name\":\"%s\",\"description\":\"%s\",\"public\":\"%s\"}"
			.formatted(playlist.getName(), "New playlist", "false");
	}
}
