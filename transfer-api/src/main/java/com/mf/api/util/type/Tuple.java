package com.mf.api.util.type;

import lombok.Data;

@Data
public class Tuple <T, K> {

	private final T first;
	private final K second;

	public static <T, K> Tuple<T, K> of(T first, K second) {
		return new Tuple<>(first, second);
	}
}
