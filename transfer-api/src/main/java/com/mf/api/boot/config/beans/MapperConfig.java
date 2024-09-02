package com.mf.api.boot.config.beans;

import com.mf.api.adapter.out.musicservice.mapper.spotify.SpotifyPaginationMapper;
import com.mf.api.adapter.out.musicservice.mapper.spotify.SpotifyPlaylistMapper;
import com.mf.api.adapter.out.musicservice.mapper.spotify.SpotifyTrackMapper;
import com.mf.api.adapter.out.musicservice.mapper.ytmusic.YTMusicPaginationMapper;
import com.mf.api.adapter.out.musicservice.mapper.ytmusic.YTMusicPlaylistMapper;
import com.mf.api.adapter.out.musicservice.mapper.ytmusic.YTMusicTrackMapper;
import com.mf.api.domain.service.mapper.OAuth2TokenMapper;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfig {

	@Bean
	public OAuth2TokenMapper tokenMapper() {
		return new OAuth2TokenMapper();
	}

	@Bean
	public SpotifyPaginationMapper spotifyPaginationMapper() {
		return new SpotifyPaginationMapper();
	}

	@Bean
	public SpotifyTrackMapper spotifyTrackMapper() {
		return new SpotifyTrackMapper();
	}

	@Bean
	public SpotifyPlaylistMapper spotifyPlaylistMapper() {
		return new SpotifyPlaylistMapper();
	}

	@Bean
	public YTMusicPaginationMapper ytMusicPaginationMapper() {
		return new YTMusicPaginationMapper();
	}

	@Bean
	public YTMusicTrackMapper ytMusicTrackMapper() {
		return new YTMusicTrackMapper();
	}

	@Bean
	public YTMusicPlaylistMapper ytMusicPlaylistMapper() {
		return new YTMusicPlaylistMapper();
	}
}
