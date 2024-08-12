package com.mf.api.domain.entity;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Playlist {

	private String id;
	private String name;
	private List<Track> tracks;
}
