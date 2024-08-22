package com.mf.api.usecase.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferResult <T> {

	private int transferredCount;
	private T failed;

	public static <T> TransferResult<T> of(int transferredCount, T failed) {
		return new TransferResult<>(transferredCount, failed);
	}
}
