package com.mf.api.adapter.in.rest.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@ApiModel(
	value = "TransferResult",
	description = "Details about music transfer operation result"
)
public class TransferResult {

	@ApiModelProperty("The number of transferred tracks")
	private int transferredTracks;

	@ApiModelProperty("The number of transferred playlists")
	private int transferredPlaylists;

	@ApiModelProperty("Failed to transfer ")
	private List<TrackRestEntity> failedToTransfer;
}
