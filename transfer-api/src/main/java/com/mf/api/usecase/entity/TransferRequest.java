package com.mf.api.usecase.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransferRequest {

	private String jwt;
	private String source;
	private String target;
	private boolean transferPlaylists;
}
