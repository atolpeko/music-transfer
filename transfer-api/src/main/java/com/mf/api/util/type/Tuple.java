package com.mf.api.util.type;

import lombok.Data;

@Data
public class Tuple <T, K> {

	private final T first;
	private final K second;
}
