package com.mf.api.usecase.valueobject;

import com.mf.api.domain.entity.OAuth2Token;
import com.mf.api.port.MusicServicePort;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransferContext {

	private String source;
	private String target;
	private MusicServicePort sourceSvc;
	private MusicServicePort targetSvc;
	private OAuth2Token sourceToken;
	private OAuth2Token targetToken;
}
