package com.mf.serviceconfig.rest.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@ApiModel(
    value = "ErrorResponse",
    description = "Details about any error response from the service"
)
public class ErrorResponse {

    @ApiModelProperty("The timestamp at which the error occurred")
    private final long timestamp;

    @ApiModelProperty("The HTTP status code of the error")
    private final int status;

    @ApiModelProperty("Description of the error")
    private final String error;

    @ApiModelProperty("The endpoint at which the error occurred")
    private final String path;
}
