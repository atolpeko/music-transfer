package com.mf.api.adapter.out.musicservice;

import com.mf.api.adapter.out.musicservice.mapper.ytmusic.YTMusicPaginationMapper;
import com.mf.api.adapter.out.musicservice.mapper.ytmusic.YTMusicPlaylistMapper;
import com.mf.api.adapter.out.musicservice.mapper.ytmusic.YTMusicTrackMapper;
import com.mf.api.adapter.out.musicservice.properties.DefaultMusicServiceProperties;
import com.mf.api.domain.entity.OAuth2Token;
import com.mf.api.domain.entity.Playlist;
import com.mf.api.domain.entity.Track;
import com.mf.api.domain.valueobject.TrackSearchCriteria;
import com.mf.api.util.Page;
import com.mf.queue.service.RequestQueue;

import java.util.List;
import java.util.Optional;

public class YTMusicAdapter extends BaseMusicServiceAdapter {

	private final DefaultMusicServiceProperties properties;
	private final YTMusicPaginationMapper paginationMapper;
	private final YTMusicTrackMapper trackMapper;
	private final YTMusicPlaylistMapper playlistMapper;

	public YTMusicAdapter(
		RequestQueue requestQueue,
		DefaultMusicServiceProperties properties,
		YTMusicPaginationMapper paginationMapper,
		YTMusicTrackMapper trackMapper,
		YTMusicPlaylistMapper playlistMapper
	) {
		super(requestQueue, properties);
		this.properties = properties;
		this.paginationMapper = paginationMapper;
		this.trackMapper = trackMapper;
		this.playlistMapper = playlistMapper;
	}

	@Override
	public String toString() {
		return properties.name();
	}

	@Override
	public Page<Track> likedTracks(OAuth2Token token, String next) {
		var url = "%s?myRating=like&part=snippet&maxResults=%s%s".formatted(
			getUrl(properties.likedTracksUrl()),
			properties.pageSize(),
			(next != null) ? "&pageToken=" + next : ""
		);

		return getForList(url, token, paginationMapper::map, trackMapper::mapList);
	}

	@Override
	public Page<Track> playlistTracks(OAuth2Token token, String playlistId, String next) {
		var url = "%s?part=snippet&playlistId=%s&maxResults=%s%s".formatted(
			getUrl(properties.playlistTracksUrl()),
			playlistId,
			properties.pageSize(),
			(next != null) ? "&pageToken=" + next : ""
		);

		return getForList(url, token, paginationMapper::map, trackMapper::mapList);
	}

	@Override
	public Page<Playlist> playlists(OAuth2Token token, String next) {
		var url = "%s?mine=true&part=snippet&maxResults=%s%s".formatted(
			getUrl(properties.playlistsUrl()),
			properties.pageSize(),
			(next != null) ? "&pageToken=" + next : ""
		);

		return getForList(url, token, paginationMapper::map, playlistMapper::mapList);
	}

	@Override
	public void likeTrack(OAuth2Token token, Track track) {

		// Impossible to implement because of YouTube request quota
		throw new UnsupportedOperationException("Not supported");
	}

	@Override
	public Optional<Track> searchTracks(OAuth2Token token, TrackSearchCriteria criteria) {

		// Impossible to implement because of YouTube request quota
		throw new UnsupportedOperationException("Not supported");
	}

	@Override
	public String createPlaylist(OAuth2Token token, Playlist playlist) {

		// Impossible to implement because of YouTube request quota
		throw new UnsupportedOperationException("Not supported");
	}

	@Override
	public void addToPlaylist(OAuth2Token token, String playlistId, List<Track> tracks) {

		// Impossible to implement because of YouTube request quota
		throw new UnsupportedOperationException("Not supported");
	}
}
