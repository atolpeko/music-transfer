package com.mf.api.domain.valueobject;

import java.util.ArrayList;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TrackSearchCriteria {

	@Builder.Default
	private String trackName = "";

	@Builder.Default
	private String albumName = "";

	@Builder.Default
	private List<String> artists = new ArrayList<>();
}
