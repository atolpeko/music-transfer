package com.mf.auth.boot.config.beans;

import com.mf.auth.adapter.out.musicservice.SpotifyAdapter;
import com.mf.auth.adapter.out.musicservice.YTMusicAdapter;
import com.mf.auth.adapter.properties.MusicServiceProperties;
import com.mf.auth.port.MusicServicePort;
import com.mf.queue.service.RequestQueue;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MusicServiceConfig {

	@Bean
	public MusicServicePort spotifyAdapter(
		@Qualifier("spotifyProperties") MusicServiceProperties properties,
		RequestQueue requestQueue
	) {
		return new SpotifyAdapter(properties, requestQueue);
	}

	@Bean
	public MusicServicePort ytMusicAdapter(
		@Qualifier("ytMusicProperties") MusicServiceProperties properties,
		RequestQueue requestQueue
	) {
		return new YTMusicAdapter(properties, requestQueue);
	}
}
