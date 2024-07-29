package com.mf.auth.boot.config.beans;

import com.mf.auth.adapter.out.musicservice.SpotifyAdapter;
import com.mf.auth.adapter.properties.MusicServiceProperties;
import com.mf.auth.port.MusicServicePort;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class MusicServiceConfig {

	@Bean
	public MusicServicePort spotifyAdapter(
		@Qualifier("spotifyProperties") MusicServiceProperties properties,
		RestTemplate restTemplate
	) {
		return new SpotifyAdapter(properties, restTemplate);
	}
}
