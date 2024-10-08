package com.mf.api.usecase.entity;

import com.mf.api.usecase.valueobject.TokenMap;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransferRequest <T> {

	private String source;
	private String target;
	private TokenMap tokenMap;
	private T toTransfer;
}
