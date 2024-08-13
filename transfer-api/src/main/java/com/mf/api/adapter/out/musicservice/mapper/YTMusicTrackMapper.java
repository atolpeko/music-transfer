package com.mf.api.adapter.out.musicservice.mapper;

import com.mf.api.domain.entity.Track;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

public class YTMusicTrackMapper {

	public Track map(LinkedHashMap<Object, Object> restResponse) {
		var data = (LinkedHashMap<?, ?>) restResponse.get("snippet");
		var artist = Optional.ofNullable((String) data.get("videoOwnerChannelTitle"))
			.orElse((String) data.get("channelTitle"))
			.replace(" - topic", "");

		return Track.builder()
			.id((String) restResponse.get("id"))
			.name((String) data.get("title"))
			.artists(List.of(artist))
			.build();
	}

	public List<Track> mapList(LinkedHashMap<Object, Object> restResponse) {
		var tracks = (List<LinkedHashMap>) restResponse.get("items");
		return tracks.stream()
			.map(this::map)
			.toList();
	}
}
