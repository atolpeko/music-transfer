package com.mf.api.adapter.out.musicservice;

import com.mf.api.adapter.out.musicservice.mapper.spotify.SpotifyPaginationMapper;
import com.mf.api.adapter.out.musicservice.mapper.spotify.SpotifyPlaylistMapper;
import com.mf.api.adapter.out.musicservice.mapper.spotify.SpotifyTrackMapper;
import com.mf.api.adapter.out.musicservice.properties.SpotifyProperties;
import com.mf.api.domain.entity.OAuth2Token;
import com.mf.api.domain.entity.Playlist;
import com.mf.api.domain.entity.Track;
import com.mf.api.domain.valueobject.TrackSearchCriteria;
import com.mf.api.util.Page;
import com.mf.queue.service.RequestQueue;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.http.HttpMethod;

public class SpotifyAdapter extends BaseMusicServiceAdapter {

	private final SpotifyProperties properties;
	private final SpotifyPaginationMapper paginationMapper;
	private final SpotifyTrackMapper trackMapper;
	private final SpotifyPlaylistMapper playlistMapper;

	public SpotifyAdapter(
		RequestQueue requestQueue,
		SpotifyProperties properties,
		SpotifyPaginationMapper paginationMapper,
		SpotifyTrackMapper trackMapper,
		SpotifyPlaylistMapper playlistMapper
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
		request(
			getUrl(properties.trackLikeUrl()),
			HttpMethod.PUT,
			token,
			trackMapper.idsToJson(track),
			Void.class,
			true
		);
	}

	@Override
	public void trackBulkLike(OAuth2Token token, List<Track> tracks) {
		request(
			getUrl(properties.trackLikeUrl()),
			HttpMethod.PUT,
			token,
			trackMapper.idsToJson(tracks),
			Void.class,
			true
		);
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
		var userId = getUserId(token);
		var url = getUrl(properties.createPlaylistUrl().replace("{user-id}", userId));
		var response = request(
			url,
			HttpMethod.POST,
			token,
			playlistMapper.mapToJson(playlist),
			LinkedHashMap.class,
			true
		);

		var body = Objects.requireNonNull(response.getBody());
		return playlistMapper.map(body).getServiceId();
	}

	private String getUserId(OAuth2Token token) {
		var response = request(
			getUrl(properties.meUrl()),
			HttpMethod.GET,
			token,
			null,
			LinkedHashMap.class,
			true
		);

		var body = Objects.requireNonNull(response.getBody());
		return (String) body.get("id");
	}

	@Override
	public void addToPlaylist(OAuth2Token token, String playlistId, List<Track> tracks) {
		addToPlaylist(token, playlistId, tracks, 50);
	}

	private void addToPlaylist(
		OAuth2Token token,
		String playlistId,
		List<Track> tracks,
		int batchSize
	) {
		if (tracks == null || tracks.isEmpty()) {
			return;
		}

		int end = Math.min(batchSize, tracks.size());
		var batch = tracks.subList(0, end);
		add(batch, playlistId, token);

		if (tracks.size() > batchSize) {
			tracks = tracks.subList(end, tracks.size());
			addToPlaylist(token, playlistId, tracks, batchSize);;
		}
	}

	private void add(List<Track> tracks, String playlistId, OAuth2Token token) {
		var url = getUrl(properties.playlistTracksUrl().replace("{id}", playlistId));
		request(
			url,
			HttpMethod.POST,
			token,
			trackMapper.urisToJson(tracks),
			Void.class,
			true
		);
	}
}
