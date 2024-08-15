package com.mf.api.boot.config.beans;

import com.mf.api.adapter.out.musicservice.SpotifyAdapter;
import com.mf.api.adapter.out.musicservice.YTMusicAdapter;
import com.mf.api.adapter.out.musicservice.mapper.SpotifyPlaylistMapper;
import com.mf.api.adapter.out.musicservice.mapper.SpotifyTrackMapper;
import com.mf.api.adapter.out.musicservice.mapper.YTMusicPlaylistMapper;
import com.mf.api.adapter.out.musicservice.mapper.YTMusicTrackMapper;
import com.mf.api.adapter.out.musicservice.properties.DefaultMusicServiceProperties;
import com.mf.api.adapter.out.musicservice.properties.SpotifyProperties;
import com.mf.api.port.MusicServicePort;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.retry.Retry;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class MusicServiceConfig {

	@Bean
	public MusicServicePort spotifyAdapter(
		RestTemplate restTemplate,
		CircuitBreaker breaker,
		Retry retry,
		SpotifyProperties properties,
		SpotifyTrackMapper trackMapper,
		SpotifyPlaylistMapper playlistMapper
	) {
		return new SpotifyAdapter(
			restTemplate,
			breaker,
			retry,
			properties,
			trackMapper,
			playlistMapper
		);
	}

	@Bean
	public MusicServicePort ytMusicAdapter(
		RestTemplate restTemplate,
		CircuitBreaker breaker,
		Retry retry,
		@Qualifier("ytMusicProperties") DefaultMusicServiceProperties properties,
		YTMusicTrackMapper trackMapper,
		YTMusicPlaylistMapper playlistMapper
	) {
		return new YTMusicAdapter(
			restTemplate,
			breaker,
			retry,
			properties,
			trackMapper,
			playlistMapper
		);
	}
}
