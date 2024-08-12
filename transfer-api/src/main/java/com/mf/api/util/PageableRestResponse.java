package com.mf.api.util;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PageableRestResponse<T> {

	private List<T> items;
	private Integer prevOffset;
	private boolean hasNext;

	public boolean hasNext() {
		return hasNext;
	}
}
