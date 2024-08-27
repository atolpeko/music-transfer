package com.mf.api.adapter.in.rest.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
@ApiModel(
	value = "PlaylistRestEntity",
	description = "Details about a playlist"
)
public class PlaylistRestEntity {

	@ApiModelProperty("Playlist ID")
	@NotBlank(message = "Playlist ID is required")
	private String id;

	@ApiModelProperty("Playlist name")
	@NotBlank(message = "Playlist name is required")
	private String name;

	@ApiModelProperty("Image URL")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private String imageUrl;

	@Valid
	@JsonInclude(Include.NON_NULL)
	@ApiModelProperty("Playlist tracks")
	private List<TrackRestEntity> tracks;
}
