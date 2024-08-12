package com.mf.api.util;

import lombok.Data;

@Data
public class Param {

	private final String key;
	private final String value;

	public static Param of(String key, String value) {
		return new Param(key, value);
	}

	public static Param of(String key) {
		return new Param(key, null);
	}

	@Override
	public String toString() {
		return key + "=" + value;
	}
}
