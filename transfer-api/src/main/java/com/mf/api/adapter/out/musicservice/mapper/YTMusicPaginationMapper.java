package com.mf.api.adapter.out.musicservice.mapper;

import java.util.LinkedHashMap;

public class YTMusicPaginationMapper {

	public String map(LinkedHashMap restResponse) {
		return (String) restResponse.get("nextPageToken");
	}
}
