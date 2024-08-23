package com.mf.auth.adapter.in.rest.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.time.LocalDateTime;
import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@ApiModel(
	value = "TokenRestEntity",
	description = "Details about a token"
)
@NoArgsConstructor
@AllArgsConstructor
public class TokenRestEntity {

	@ApiModelProperty("Token value")
	@NotBlank(message = "Token value is required")
	private String value;

	@ApiModelProperty("Token expiration date")
	private LocalDateTime expiresAt;
}
