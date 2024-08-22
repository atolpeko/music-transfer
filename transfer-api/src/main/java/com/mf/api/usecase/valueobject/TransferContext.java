package com.mf.api.usecase.valueobject;

import com.mf.api.domain.entity.OAuth2Token;
import com.mf.api.port.MusicServicePort;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransferContext <T> {

	private String source;
	private String target;
	private MusicServicePort service;
	private OAuth2Token token;
	private T toTransfer;
}
