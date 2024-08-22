package com.mf.api.adapter.in.rest.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
@ApiModel(
	value = "TracksRestEntity",
	description = "Details about a list of tracks"
)
public class TracksRestEntity {

	@ApiModelProperty("Tracks")
	@NotNull(message = "Tracks are required")
	private List<TrackRestEntity> tracks;
}
