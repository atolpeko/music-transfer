package com.mf.api.adapter.in.rest.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@ApiModel(
	value = "TransferResultRestEntity",
	description = "Details about music transfer operation result"
)
public class TransferResultRestEntity <T> {

	@ApiModelProperty("The number of transferred items")
	private int transferred;

	@ApiModelProperty("Items failed to transfer ")
	private T failedToTransfer;
}
