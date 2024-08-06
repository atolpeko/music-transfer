package com.mf.api.domain.valueobject;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TrackSearchCriteria {
	private String trackName;
	private String albumName;
	private List<String> artists;
}
