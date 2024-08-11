package com.mf.auth.adapter.out.musicservice;

import com.mf.auth.adapter.properties.MusicServiceProperties;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.client.RestTemplate;

@Log4j2
public class YTMusicAdapter extends StandardOauth2MusicService {

	public YTMusicAdapter(
		MusicServiceProperties properties,
		RestTemplate restTemplate,
		CircuitBreaker breaker
	) {
		super(properties, restTemplate, breaker);
	}

	@Override
	public String toString() {
		return properties.name();
	}
}
