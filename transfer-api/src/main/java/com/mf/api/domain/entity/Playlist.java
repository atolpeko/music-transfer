package com.mf.api.domain.entity;

import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Playlist extends Transferable {

	private List<Track> tracks;

	@Builder
	public Playlist(String serviceId, String name, String imgUrl, List<Track> tracks) {
		super(serviceId, name, imgUrl);
		this.tracks = tracks;
	}
}
