package com.mf.api.util;

import java.util.List;
import lombok.Data;

@Data
public class Page<T> {

	private final List<T> items;
	private final String next;

	public static <T> Page<T> of(List<T> items) {
		return new Page<>(items, null);
	}

	public static <T> Page<T> of(List<T> items, String next) {
		return new Page<>(items, next);
	}
}
