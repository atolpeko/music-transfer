package com.mf.serviceconfig.rest.entity;

import com.mf.serviceconfig.entity.Service;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

import lombok.Data;

@Data
@ApiModel(value = "Response")
public class Response {

	@ApiModelProperty("Source services")
	private final List<Service> source;

	@ApiModelProperty("Source services")
	private final List<Service> target;
}
