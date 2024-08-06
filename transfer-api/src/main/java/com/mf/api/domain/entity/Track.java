package com.mf.api.domain.entity;

import java.util.List;
import lombok.Data;

@Data
public class Track {
	private String id;
	private String name;
	private String albumName;
	private List<String> artists;
}
