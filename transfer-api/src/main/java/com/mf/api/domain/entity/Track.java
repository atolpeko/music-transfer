package com.mf.api.domain.entity;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Track {

	private String id;
	private String name;
	private String albumName;
	private List<String> artists;
}
