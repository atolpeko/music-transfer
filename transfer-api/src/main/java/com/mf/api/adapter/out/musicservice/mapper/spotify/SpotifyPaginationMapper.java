package com.mf.api.adapter.out.musicservice.mapper.spotify;

import java.util.LinkedHashMap;

public class SpotifyPaginationMapper {

	public String map(LinkedHashMap restResponse) {
		return (String) restResponse.get("next");
	}
}
