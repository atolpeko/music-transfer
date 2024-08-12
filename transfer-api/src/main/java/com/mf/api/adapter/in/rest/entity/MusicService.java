package com.mf.api.adapter.in.rest.entity;

import io.swagger.annotations.ApiModel;

@ApiModel(
	value = "MusicService",
	description = "Music service name"
)
public enum MusicService {

	SPOTIFY,
	YT_MUSIC
}
