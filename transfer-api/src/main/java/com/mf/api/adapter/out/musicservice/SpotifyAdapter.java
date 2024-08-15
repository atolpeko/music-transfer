package com.mf.api.adapter.out.musicservice;

import com.mf.api.adapter.out.musicservice.mapper.SpotifyPlaylistMapper;
import com.mf.api.adapter.out.musicservice.mapper.SpotifyTrackMapper;
import com.mf.api.adapter.out.musicservice.properties.SpotifyProperties;
import com.mf.api.domain.entity.OAuth2Token;
import com.mf.api.domain.entity.Playlist;
import com.mf.api.domain.entity.Track;
import com.mf.api.domain.valueobject.TrackSearchCriteria;
import com.mf.api.port.exception.MusicServiceException;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.retry.Retry;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

public class SpotifyAdapter extends BaseMusicServiceAdapter {

	private final SpotifyProperties properties;
	private final SpotifyTrackMapper trackMapper;
	private final SpotifyPlaylistMapper playlistMapper;

	public SpotifyAdapter(
		RestTemplate restTemplate,
		CircuitBreaker circuitBreaker,
		Retry retry,
		SpotifyProperties properties,
		SpotifyTrackMapper trackMapper,
		SpotifyPlaylistMapper playlistMapper
	) {
		super(restTemplate, circuitBreaker, retry);
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
			var url = "%s?limit=%s".formatted(
				properties.likedTracksUrl(),
				properties.pageSize()
			);
			var tracks = new LinkedList<Track>();

			do {
				var response = execRequest(url, HttpMethod.GET, token, LinkedHashMap.class);
				var body = Objects.requireNonNull(response.getBody());
				tracks.addAll(trackMapper.mapList(body));
				url = (String) body.get("next");
			} while (url != null);

			return tracks;
		} catch (MusicServiceException e) {
			throw e;
		} catch (Exception e) {
			throw new MusicServiceException(e.getMessage(), e.getCause());
		}
	}

	@Override
	public void likeTrack(OAuth2Token token, Track track) {
		try {
			execRequest(
				properties.trackLikeUrl(),
				HttpMethod.PUT,
				token,
				trackMapper.idsToJson(track),
				Void.class
			);
		} catch (MusicServiceException e) {
			throw e;
		} catch (Exception e) {
			throw new MusicServiceException(e.getMessage(), e.getCause());
		}
	}

	@Override
	public void trackBulkLike(OAuth2Token token, List<Track> tracks) {
		try {
			execRequest(
				properties.trackLikeUrl(),
				HttpMethod.PUT,
				token,
				trackMapper.idsToJson(tracks),
				Void.class
			);
		} catch (MusicServiceException e) {
			throw e;
		} catch (Exception e) {
			throw new MusicServiceException(e.getMessage(), e.getCause());
		}
	}

	@Override
	public Optional<Track> searchTracks(OAuth2Token token, TrackSearchCriteria criteria) {
		try {
			var url = "%s?q=%s&offset=0&limit=1&type=track".formatted(
				properties.searchTracksUrl(),
				buildSearchQuery(criteria)
			);
			var response = execRequest(
				url,
				HttpMethod.GET,
				token,
				LinkedHashMap.class
			);

			var body = Objects.requireNonNull(response.getBody());
			var found = trackMapper.mapList(body);
			return (found.isEmpty())
				? Optional.empty()
				: Optional.of(found.get(0));
		} catch (NullPointerException e) {
			return Optional.empty();
		} catch (Exception e) {
			throw new MusicServiceException(e.getMessage(), e.getCause());
		}
	}

	private String buildSearchQuery(TrackSearchCriteria criteria) {
		var builder = new StringBuilder();
		if (criteria.getTrackName().isBlank()) {
			throw new IllegalArgumentException("At least track name should be set");
		} else {
			builder.append(criteria.getTrackName());
		}

		if (!criteria.getAlbumName().isBlank()) {
			builder.append(" album:").append((criteria.getAlbumName()));
		}
		if (!criteria.getArtists().isEmpty()) {
			builder.append(" artist:").append(criteria.getArtists().get(0));
		}

		return builder.toString();
	}

	@Override
	public List<Playlist> playlists(OAuth2Token token) {
		try {
			var playlists = fetchPlaylists(token);
			for (var playlist : playlists) {
				var tracks = fetchPlaylistTracks(playlist.getId(), token);
				playlist.setTracks(tracks);
			}

			return playlists;
		} catch (MusicServiceException e) {
			throw e;
		} catch (Exception e) {
			throw new MusicServiceException(e.getMessage(), e.getCause());
		}
	}

	private List<Playlist> fetchPlaylists(OAuth2Token token) throws Exception {
		var url = "%s?limit=%s".formatted(
			properties.playlistsUrl(),
			properties.pageSize()
		);
		var playlists = new LinkedList<Playlist>();

		do {
			var response = execRequest(url, HttpMethod.GET, token, LinkedHashMap.class);
			var body = Objects.requireNonNull(response.getBody());
			playlists.addAll(playlistMapper.mapList(body));
			url = (String) body.get("next");
		} while (url != null);

		return playlists;
	}

	private List<Track> fetchPlaylistTracks(String playlistId, OAuth2Token token) throws Exception {
		var url = "%s?limit=%s".formatted(
			properties.playlistTracksUrl(),
			properties.pageSize()
		).replace("{id}", playlistId);;
		var tracks = new LinkedList<Track>();

		do {
			var response = execRequest(url, HttpMethod.GET, token, LinkedHashMap.class);
			var body = Objects.requireNonNull(response.getBody());
			tracks.addAll(trackMapper.mapList(body));
			url = (String) body.get("next");
		} while (url != null);

		return tracks;
	}

	@Override
	public String createPlaylist(OAuth2Token token, Playlist playlist) {
		try {
			var userId = getUserId(token);
			var url = properties.createPlaylistUrl().replace("{user-id}", userId);
			var response = execRequest(
				url,
				HttpMethod.POST,
				token,
				playlistMapper.mapToJson(playlist),
				LinkedHashMap.class
			);

			var body = Objects.requireNonNull(response.getBody());
			return playlistMapper.map(body).getId();
		} catch (MusicServiceException e) {
			throw e;
		} catch (Exception e) {
			throw new MusicServiceException(e.getMessage(), e.getCause());
		}
	}

	private String getUserId(OAuth2Token token) throws Exception {
		var response = execRequest(
			properties.meUrl(),
			HttpMethod.GET,
			token,
			LinkedHashMap.class
		);

		var body = Objects.requireNonNull(response.getBody());
		return (String) body.get("id");
	}

	@Override
	public void addToPlaylist(OAuth2Token token, String playlistId, List<Track> tracks) {
		try {
			var url = properties.playlistTracksUrl().replace("{id}", playlistId);
			execRequest(
				url,
				HttpMethod.POST,
				token,
				trackMapper.urisToJson(tracks),
				Void.class
			);
		} catch (MusicServiceException e) {
			throw e;
		} catch (Exception e) {
			throw new MusicServiceException(e.getMessage(), e.getCause());
		}
	}
}
