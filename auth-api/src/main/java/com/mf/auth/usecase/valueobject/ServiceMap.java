package com.mf.auth.usecase.valueobject;

import com.mf.auth.port.MusicServicePort;
import java.util.Map;
import lombok.AllArgsConstructor;

/**
 * Mapping of service names to services.
 */
@AllArgsConstructor
public class ServiceMap {

	private final Map<String, MusicServicePort> nameToService;

	public MusicServicePort get(String name) {
		return nameToService.get(name);
	}
}
