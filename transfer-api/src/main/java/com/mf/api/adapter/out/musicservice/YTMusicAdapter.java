package com.mf.api.adapter.out.musicservice;

import com.mf.api.adapter.out.musicservice.mapper.YTMusicPlaylistMapper;
import com.mf.api.adapter.out.musicservice.mapper.YTMusicTrackMapper;
import com.mf.api.adapter.out.musicservice.properties.DefaultMusicServiceProperties;
import com.mf.api.domain.entity.OAuth2Token;
import com.mf.api.domain.entity.Playlist;
import com.mf.api.domain.entity.Track;
import com.mf.api.domain.valueobject.TrackSearchCriteria;
import com.mf.api.port.exception.AccessException;
import com.mf.api.port.exception.MusicServiceException;
import com.mf.api.util.restclient.RestClient;
import com.mf.api.util.restclient.RestRequest;
import com.mf.api.util.restclient.Param;
import com.mf.api.util.restclient.exception.AuthException;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpMethod;

public class YTMusicAdapter extends BaseMusicServiceAdapter {
	protected final DefaultMusicServiceProperties properties;
	protected final YTMusicTrackMapper trackMapper;
	protected final YTMusicPlaylistMapper playlistMapper;

	public YTMusicAdapter(
		RestClient restClient,
		DefaultMusicServiceProperties properties,
		YTMusicTrackMapper trackMapper,
		YTMusicPlaylistMapper playlistMapper
	) {
		super(restClient);
		this.properties = properties;
		this.trackMapper = trackMapper;
		this.playlistMapper = playlistMapper;
	}

	@Override
	public String toString() {
		return properties.name();
	}

	@Override
	public List<Track> likedTracks(OAuth2Token token) {
		try {
			var request = RestRequest.builder()
				.url(properties.likedTracksUrl())
				.method(HttpMethod.GET)
				.params(List.of(
					Param.of("myRating", "like"),
					Param.of("part", "snippet, contentDetails")))
				.token(token)
				.limit(properties.pageSize())
				.retryIfFails(true)
				.build();

			return fetchAllPages(request, trackMapper::mapList);
		} catch (AuthException e) {
			throw new AccessException("Authorization with YouTube Music failed", e);
		} catch (Exception e) {
			throw new MusicServiceException(e.getMessage(), e.getCause());
		}
	}

	@Override
	public List<Playlist> playlists(OAuth2Token token) {
		try {
			var playlists = fetchPlaylists(token);
			playlists.forEach(playlist -> {
				var tracks = fetchPlaylistTracks(playlist.getId(), token);
				playlist.setTracks(tracks);
			});

			return playlists;
		} catch (AuthException e) {
			throw new AccessException("Authorization with YouTube Music failed", e);
		} catch (Exception e) {
			throw new MusicServiceException(e.getMessage(), e.getCause());
		}
	}

	private List<Playlist> fetchPlaylists(OAuth2Token token) {
		var request = RestRequest.builder()
			.url(properties.playlistsUrl())
			.method(HttpMethod.GET)
			.params(List.of(
				Param.of("part", "snippet, status"),
				Param.of("mine", "true")
			))
			.token(token)
			.limit(properties.pageSize())
			.retryIfFails(true)
			.build();

		return fetchAllPages(request, playlistMapper::mapList);
	}

	private List<Track> fetchPlaylistTracks(String playlistId, OAuth2Token token) {
		var request = RestRequest.builder()
			.url(properties.playlistTracksUrl())
			.method(HttpMethod.GET)
			.params(List.of(
				Param.of("part", "snippet, status"),
				Param.of("playlistId", playlistId)
			))
			.token(token)
			.limit(properties.pageSize())
			.retryIfFails(true)
			.build();

		return fetchAllPages(request, trackMapper::mapList);
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
