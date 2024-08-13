package com.mf.api.adapter.out.musicservice;

import com.mf.api.adapter.out.musicservice.mapper.SpotifyPlaylistMapper;
import com.mf.api.adapter.out.musicservice.mapper.SpotifyTrackMapper;
import com.mf.api.adapter.out.musicservice.properties.SpotifyProperties;
import com.mf.api.domain.entity.OAuth2Token;
import com.mf.api.domain.entity.Playlist;
import com.mf.api.domain.entity.Track;
import com.mf.api.domain.valueobject.TrackSearchCriteria;
import com.mf.api.port.exception.AccessException;
import com.mf.api.port.exception.MusicServiceException;
import com.mf.api.util.restclient.RestRequest;
import com.mf.api.util.restclient.RestClient;
import com.mf.api.util.restclient.Param;
import com.mf.api.util.restclient.exception.AuthException;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpMethod;

public class SpotifyAdapter extends BaseMusicServiceAdapter {
	protected final SpotifyProperties properties;
	protected final SpotifyTrackMapper trackMapper;
	protected final SpotifyPlaylistMapper playlistMapper;

	public SpotifyAdapter(
		RestClient restClient,
		SpotifyProperties properties,
		SpotifyTrackMapper trackMapper,
		SpotifyPlaylistMapper playlistMapper
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
				.token(token)
				.limit(properties.pageSize())
				.retryIfFails(true)
				.build();

			return fetchAllPages(request, trackMapper::mapList);
		} catch (AuthException e) {
			throw new AccessException("Authorization with Spotify failed", e);
		} catch (Exception e) {
			throw new MusicServiceException(e.getMessage(), e.getCause());
		}
	}

	@Override
	public void likeTrack(OAuth2Token token, Track track) {
		try {
			var request = RestRequest.builder()
				.url(properties.trackLikeUrl())
				.method(HttpMethod.PUT)
				.token(token)
				.json(trackMapper.idsToJson(track))
				.retryIfFails(true)
				.build();

			restClient.request(request);
		} catch (AuthException e) {
			throw new AccessException("Authorization with Spotify failed", e);
		} catch (Exception e) {
			throw new MusicServiceException(e.getMessage(), e.getCause());
		}
	}

	@Override
	public void trackBulkLike(OAuth2Token token, List<Track> tracks) {
		try {
			var request = RestRequest.builder()
				.url(properties.trackLikeUrl())
				.method(HttpMethod.PUT)
				.token(token)
				.json(trackMapper.idsToJson(tracks))
				.retryIfFails(true)
				.build();

			restClient.request(request);
		} catch (AuthException e) {
			throw new AccessException("Authorization with Spotify failed", e);
		} catch (Exception e) {
			throw new MusicServiceException(e.getMessage(), e.getCause());
		}
	}

	@Override
	public Optional<Track> searchTracks(OAuth2Token token, TrackSearchCriteria criteria) {
		try {
			var request = RestRequest.builder()
				.url(properties.searchTracksUrl())
				.method(HttpMethod.GET)
				.params(List.of(
					Param.of("q", buildSearchQuery(criteria)),
					Param.of("type", "track")
				))
				.token(token)
				.offset(0)
				.limit(5)
				.retryIfFails(true)
				.build();

			var response = restClient.request(request, trackMapper::mapList);
			if (response.isEmpty()) {
				return Optional.empty();
			}

			return Optional.of(response.get(0));
		} catch (AuthException e) {
			throw new AccessException("Authorization with Spotify failed", e);
		} catch (Exception e) {
			throw new MusicServiceException(e.getMessage(), e.getCause());
		}
	}

	private String buildSearchQuery(TrackSearchCriteria criteria) {
		var builder = new StringBuilder();
		if (!criteria.getTrackName().isBlank()) {
			builder.append(criteria.getTrackName());
		}
		if (!criteria.getAlbumName().isBlank()) {
			builder.append(" album:").append((criteria.getAlbumName()));
		}
		if (!criteria.getArtists().isEmpty()) {
			builder.append(" artist:").append(criteria.getArtists().get(0));
		}

		return builder.toString().replaceAll(" ", "%20");
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
			throw new AccessException("Authorization with Spotify failed", e);
		} catch (Exception e) {
			throw new MusicServiceException(e.getMessage(), e.getCause());
		}
	}

	private List<Playlist> fetchPlaylists(OAuth2Token token) {
		var request = RestRequest.builder()
			.url(properties.playlistsUrl())
			.method(HttpMethod.GET)
			.token(token)
			.limit(properties.pageSize())
			.retryIfFails(true)
			.build();

		return fetchAllPages(request, playlistMapper::mapList);
	}

	private List<Track> fetchPlaylistTracks(String playlistId, OAuth2Token token) {
		var url = properties.playlistTracksUrl().replace("{id}", playlistId);
		var request = RestRequest.builder()
			.url(url)
			.method(HttpMethod.GET)
			.token(token)
			.limit(properties.pageSize())
			.retryIfFails(true)
			.build();

		return fetchAllPages(request, trackMapper::mapList);
	}

	@Override
	public String createPlaylist(OAuth2Token token, Playlist playlist) {
		try {
			var userId = getUserId(token);
			var url = properties.createPlaylistUrl().replace("{user-id}", userId);
			var request = RestRequest.builder()
				.url(url)
				.method(HttpMethod.POST)
				.token(token)
				.json(playlistMapper.mapToJson(playlist))
				.retryIfFails(true)
				.build();

			return restClient.request(request, playlistMapper::map).getId();
		} catch (AuthException e) {
			throw new AccessException("Authorization with Spotify failed", e);
		} catch (Exception e) {
			throw new MusicServiceException(e.getMessage(), e.getCause());
		}
	}

	private String getUserId(OAuth2Token token) {
		var request = RestRequest.builder()
			.url(properties.meUrl())
			.method(HttpMethod.GET)
			.token(token)
			.retryIfFails(true)
			.build();

		return restClient.request(
			request,
			map ->(String) map.get("id")
		);
	}

	@Override
	public void addToPlaylist(OAuth2Token token, String playlistId, List<Track> tracks) {
		var url = properties.playlistTracksUrl().replace("{id}", playlistId);
		var request = RestRequest.builder()
			.url(url)
			.method(HttpMethod.POST)
			.token(token)
			.json(trackMapper.urisToJson(tracks))
			.retryIfFails(true)
			.build();

		restClient.request(request);
	}
}
