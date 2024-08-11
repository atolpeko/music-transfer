package com.mf.auth.boot.config.beans;

import com.mf.auth.adapter.out.musicservice.SpotifyAdapter;
import com.mf.auth.adapter.out.musicservice.YTMusicAdapter;
import com.mf.auth.adapter.properties.MusicServiceProperties;
import com.mf.auth.port.MusicServicePort;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class MusicServiceConfig {

	@Bean
	public MusicServicePort spotifyAdapter(
		@Qualifier("spotifyProperties") MusicServiceProperties properties,
		RestTemplate restTemplate,
		CircuitBreaker breaker
	) {
		return new SpotifyAdapter(properties, restTemplate, breaker);
	}

	@Bean
	public MusicServicePort ytMusicAdapter(
		@Qualifier("ytMusicProperties") MusicServiceProperties properties,
		RestTemplate restTemplate,
		CircuitBreaker breaker
	) {
		return new YTMusicAdapter(properties, restTemplate, breaker);
	}
}
