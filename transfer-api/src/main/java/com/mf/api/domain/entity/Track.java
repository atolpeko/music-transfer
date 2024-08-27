package com.mf.api.domain.entity;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
public class Track extends Transferable {

	private String uniqueId;
	private String albumName;
	private List<String> artists;

	public Track() {
		this.uniqueId = UUID.randomUUID().toString();
	}

	@Builder
	public Track(String serviceId, String name, String imgUrl,
		String uniqueId, String albumName, List<String> artists) {
		super(serviceId, name, imgUrl);
		this.uniqueId = (uniqueId == null)
			? UUID.randomUUID().toString()
			: uniqueId;
		this.albumName = albumName;
		this.artists = artists;
	}

	public Track(String uniqueId, String albumName, List<String> artists) {
		this.uniqueId = uniqueId;
		this.albumName = albumName;
		this.artists = artists;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other == null || getClass() != other.getClass()) {
			return false;
		}

		var track = (Track) other;
		return Objects.equals(uniqueId, track.uniqueId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(uniqueId);
	}
}
