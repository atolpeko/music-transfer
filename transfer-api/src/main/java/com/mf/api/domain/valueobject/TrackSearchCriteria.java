package com.mf.api.domain.valueobject;

import java.util.ArrayList;
import java.util.List;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@Builder
@EqualsAndHashCode
public class TrackSearchCriteria {

	@Builder.Default
	private String trackName = "";

	@Builder.Default
	private String albumName = "";

	@Builder.Default
	private List<String> artists = new ArrayList<>();
}
