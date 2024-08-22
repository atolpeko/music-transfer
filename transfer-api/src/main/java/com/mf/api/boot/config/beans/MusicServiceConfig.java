package com.mf.api.boot.config.beans;

import com.mf.api.adapter.out.musicservice.SpotifyAdapter;
import com.mf.api.adapter.out.musicservice.YTMusicAdapter;
import com.mf.api.adapter.out.musicservice.mapper.SpotifyPaginationMapper;
import com.mf.api.adapter.out.musicservice.mapper.SpotifyPlaylistMapper;
import com.mf.api.adapter.out.musicservice.mapper.SpotifyTrackMapper;
import com.mf.api.adapter.out.musicservice.mapper.YTMusicPaginationMapper;
import com.mf.api.adapter.out.musicservice.mapper.YTMusicPlaylistMapper;
import com.mf.api.adapter.out.musicservice.mapper.YTMusicTrackMapper;
import com.mf.api.adapter.out.musicservice.properties.DefaultMusicServiceProperties;
import com.mf.api.adapter.out.musicservice.properties.SpotifyProperties;
import com.mf.api.port.MusicServicePort;

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
		Retry retry,
		SpotifyProperties properties,
		SpotifyPaginationMapper paginationMapper,
		SpotifyTrackMapper trackMapper,
		SpotifyPlaylistMapper playlistMapper
	) {
		return new SpotifyAdapter(
			restTemplate,
			retry,
			properties,
			paginationMapper,
			trackMapper,
			playlistMapper
		);
	}

	@Bean
	public MusicServicePort ytMusicAdapter(
		RestTemplate restTemplate,
		Retry retry,
		@Qualifier("ytMusicProperties") DefaultMusicServiceProperties properties,
		YTMusicPaginationMapper paginationMapper,
		YTMusicTrackMapper trackMapper,
		YTMusicPlaylistMapper playlistMapper
	) {
		return new YTMusicAdapter(
			restTemplate,
			retry,
			properties,
			paginationMapper,
			trackMapper,
			playlistMapper
		);
	}
}
