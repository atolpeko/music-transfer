package com.mf.api.adapter.in.rest.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
@ApiModel(
	value = "TracksRestEntity",
	description = "Details about a list of tracks"
)
public class TracksRestEntity {

	@ApiModelProperty("Tracks")
	@NotNull(message = "Tracks are required")
	@NotEmpty(message = "Tracks are required")
	@Size(max = 25, message = "Maximum tracks allowed - 25")
	private List<TrackRestEntity> tracks;
}
