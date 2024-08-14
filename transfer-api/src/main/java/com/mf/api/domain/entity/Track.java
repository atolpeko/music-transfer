package com.mf.api.domain.entity;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class Track {

	@Builder.Default
	private String uniqueId = UUID.randomUUID().toString();

	private String serviceId;
	private String name;
	private String albumName;
	private List<String> artists;

	public Track() {
		this.uniqueId = UUID.randomUUID().toString();
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
