package com.mf.api.adapter.in.rest.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

import lombok.Data;

@Data
@ApiModel(
	value = "TrackRestEntity",
	description = "Details about a track"
)
public class TrackRestEntity {

	@ApiModelProperty("Track ID")
	private String id;

	@ApiModelProperty("Track name")
	private String name;

	@ApiModelProperty("Album name")
	private String albumName;

	@ApiModelProperty("Track artists")
	private List<String> artists;
}
