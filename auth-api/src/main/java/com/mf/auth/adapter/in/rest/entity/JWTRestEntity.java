package com.mf.auth.adapter.in.rest.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.time.LocalDateTime;
import javax.validation.constraints.NotBlank;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@ApiModel(
	value = "JWTRestEntity",
	description = "Details about a JWT token"
)
public class JWTRestEntity {

	@ApiModelProperty("JWT value")
	@NotBlank(message = "JWT value is required")
	private String value;

	@ApiModelProperty("JWT expiration date")
	private LocalDateTime expiresAt;
}
