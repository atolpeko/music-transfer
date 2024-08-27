package com.mf.api.adapter.in.rest.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
@ApiModel(
	value = "TrackRestEntity",
	description = "Details about a track"
)
public class TrackRestEntity {

	@ApiModelProperty("Track ID")
	@NotBlank(message = "Track ID is required")
	private String id;

	@ApiModelProperty("Track name")
	@NotBlank(message = "Track name is required")
	private String name;

	@ApiModelProperty("Album name")
	@NotBlank(message = "Album name is required")
	private String albumName;

	@ApiModelProperty("Image URL")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private String imageUrl;

	@ApiModelProperty("Track artists")
	private List<String> artists;
}
