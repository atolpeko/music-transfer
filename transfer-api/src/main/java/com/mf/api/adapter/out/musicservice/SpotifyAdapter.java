package com.mf.api.adapter.out.musicservice;

import com.mf.api.adapter.out.musicservice.mapper.SpotifyPaginationMapper;
import com.mf.api.adapter.out.musicservice.mapper.SpotifyPlaylistMapper;
import com.mf.api.adapter.out.musicservice.mapper.SpotifyTrackMapper;
import com.mf.api.adapter.out.musicservice.properties.SpotifyProperties;
import com.mf.api.domain.entity.OAuth2Token;
import com.mf.api.domain.entity.Playlist;
import com.mf.api.domain.entity.Track;
import com.mf.api.domain.valueobject.TrackSearchCriteria;
import com.mf.api.port.exception.MusicServiceException;
import com.mf.api.util.Page;

import io.github.resilience4j.retry.Retry;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

public class SpotifyAdapter extends BaseMusicServiceAdapter {

	private final SpotifyProperties properties;
	private final SpotifyPaginationMapper paginationMapper;
	private final SpotifyTrackMapper trackMapper;
	private final SpotifyPlaylistMapper playlistMapper;

	public SpotifyAdapter(
		RestTemplate restTemplate,
		Retry retry,
		SpotifyProperties properties,
		SpotifyPaginationMapper paginationMapper,
		SpotifyTrackMapper trackMapper,
		SpotifyPlaylistMapper playlistMapper
	) {
		super(restTemplate, retry, properties);
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
		var url = next;
		if (url == null) {
			url = "%s?limit=%s".formatted(
				getUrl(properties.likedTracksUrl()),
				properties.pageSize()
			);
		}

		return getForList(url, token, paginationMapper::map, trackMapper::mapList);
	}

	@Override
	public Page<Track> playlistTracks(OAuth2Token token, String playlistId, String next) {
		var url = next;
		if (url == null) {
			url = "%s?limit=%s".formatted(
				getUrl(properties.playlistTracksUrl()),
				properties.pageSize()
			).replace("{id}", playlistId);
		}

		return getForList(url, token, paginationMapper::map, trackMapper::mapList);
	}

	@Override
	public void likeTrack(OAuth2Token token, Track track) {
		try {
			execRequest(
				getUrl(properties.trackLikeUrl()),
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
				getUrl(properties.trackLikeUrl()),
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
		var url = "%s?q=%s&offset=0&limit=1&type=track".formatted(
			getUrl(properties.searchTracksUrl()),
			buildSearchQuery(criteria)
		);

		var found = getForList(url, token, paginationMapper::map, trackMapper::mapList);
		return (found.getItems().isEmpty())
			? Optional.empty()
			: Optional.of(found.getItems().get(0));
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
	public Page<Playlist> playlists(OAuth2Token token, String next) {
		var url = next;
		if (url == null) {
			url = "%s?limit=%s".formatted(
				getUrl(properties.playlistsUrl()),
				properties.pageSize()
			);
		}

		return getForList(url, token, paginationMapper::map, playlistMapper::mapList);
	}

	@Override
	public String createPlaylist(OAuth2Token token, Playlist playlist) {
		try {
			var userId = getUserId(token);
			var url = getUrl(properties.createPlaylistUrl().replace("{user-id}", userId));
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
			getUrl(properties.meUrl()),
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
			var url = getUrl(properties.playlistTracksUrl().replace("{id}", playlistId));
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
