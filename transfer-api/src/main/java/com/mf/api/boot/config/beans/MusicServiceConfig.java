package com.mf.api.boot.config.beans;

import com.mf.api.adapter.out.musicservice.SpotifyAdapter;
import com.mf.api.adapter.out.musicservice.YTMusicAdapter;
import com.mf.api.adapter.out.musicservice.mapper.spotify.SpotifyPaginationMapper;
import com.mf.api.adapter.out.musicservice.mapper.spotify.SpotifyPlaylistMapper;
import com.mf.api.adapter.out.musicservice.mapper.spotify.SpotifyTrackMapper;
import com.mf.api.adapter.out.musicservice.mapper.ytmusic.YTMusicPaginationMapper;
import com.mf.api.adapter.out.musicservice.mapper.ytmusic.YTMusicPlaylistMapper;
import com.mf.api.adapter.out.musicservice.mapper.ytmusic.YTMusicTrackMapper;
import com.mf.api.adapter.out.musicservice.properties.DefaultMusicServiceProperties;
import com.mf.api.adapter.out.musicservice.properties.SpotifyProperties;
import com.mf.api.port.MusicServicePort;
import com.mf.queue.service.RequestQueue;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MusicServiceConfig {

	@Bean
	public MusicServicePort spotifyAdapter(
		RequestQueue requestQueue,
		SpotifyProperties properties,
		SpotifyPaginationMapper paginationMapper,
		SpotifyTrackMapper trackMapper,
		SpotifyPlaylistMapper playlistMapper
	) {
		return new SpotifyAdapter(
			requestQueue,
			properties,
			paginationMapper,
			trackMapper,
			playlistMapper
		);
	}

	@Bean
	public MusicServicePort ytMusicAdapter(
		RequestQueue requestQueue,
		@Qualifier("ytMusicProperties") DefaultMusicServiceProperties properties,
		YTMusicPaginationMapper paginationMapper,
		YTMusicTrackMapper trackMapper,
		YTMusicPlaylistMapper playlistMapper
	) {
		return new YTMusicAdapter(
			requestQueue,
			properties,
			paginationMapper,
			trackMapper,
			playlistMapper
		);
	}
}
