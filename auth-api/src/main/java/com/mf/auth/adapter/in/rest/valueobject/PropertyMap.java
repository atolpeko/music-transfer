package com.mf.auth.adapter.in.rest.valueobject;

import com.mf.auth.adapter.properties.MusicServiceProperties;
import java.util.Map;
import lombok.AllArgsConstructor;

/**
 * Mapping of service names to service properties.
 */
@AllArgsConstructor
public class PropertyMap {

	private final Map<MusicService, MusicServiceProperties> nameToProperties;

	public MusicServiceProperties get(MusicService service) {
		return nameToProperties.get(service);
	}
}
