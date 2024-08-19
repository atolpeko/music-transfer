package com.mf.api.adapter.out.musicservice;

import com.mf.api.adapter.out.musicservice.mapper.YTMusicPlaylistMapper;
import com.mf.api.adapter.out.musicservice.mapper.YTMusicTrackMapper;
import com.mf.api.adapter.out.musicservice.properties.DefaultMusicServiceProperties;
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

public class YTMusicAdapter extends BaseMusicServiceAdapter {

	protected final DefaultMusicServiceProperties properties;
	protected final YTMusicTrackMapper trackMapper;
	protected final YTMusicPlaylistMapper playlistMapper;

	public YTMusicAdapter(
		RestTemplate restTemplate,
		CircuitBreaker circuitBreaker,
		Retry retry,
		DefaultMusicServiceProperties properties,
		YTMusicTrackMapper trackMapper,
		YTMusicPlaylistMapper playlistMapper
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
			var tracks = new LinkedList<Track>();
			var templateUrl = "%s?myRating=like&part=snippet&maxResults=%s{token}"
				.formatted(
					properties.likedTracksUrl(),
					properties.pageSize()
				);

			String nextToken = null;
			do {
				var tokenArg = (nextToken != null)
					? "&pageToken=" + nextToken
					: "";
				var url = templateUrl.replace("{token}", tokenArg);

				var response = execRequest(url, HttpMethod.GET, token, LinkedHashMap.class);
				var body = Objects.requireNonNull(response.getBody());
				tracks.addAll(trackMapper.mapList(body));

				nextToken = (String) body.get("nextPageToken");
			} while (nextToken != null);

			return tracks;
		} catch (MusicServiceException e) {
			throw e;
		} catch (Exception e) {
			throw new MusicServiceException(e.getMessage(), e.getCause());
		}
	}

	@Override
	public List<Playlist> playlists(OAuth2Token token) {
		try {
			var playlists = fetchPlaylists(token);
			for (var playlist: playlists) {
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
		var playlists = new LinkedList<Playlist>();
		var url = "%s?mine=true&part=snippet&maxResults=%s{token}".formatted(
			properties.playlistsUrl(),
			properties.pageSize()
		);

		String nextToken = null;
		do {
			var tokenArg = (nextToken != null)
				? "&pageToken=" + nextToken
				: "";
			url = url.replace("{token}", tokenArg);

			var response = execRequest(url, HttpMethod.GET, token, LinkedHashMap.class);
			var body = Objects.requireNonNull(response.getBody());
			playlists.addAll(playlistMapper.mapList(body));

			nextToken = (String) body.get("nextPageToken");
			url += "&pageToken=" + nextToken;
		} while (nextToken != null);

		return playlists;
	}

	private List<Track> fetchPlaylistTracks(String playlistId, OAuth2Token token) throws Exception {
		var url = "%s?&part=snippet&playlistId=%s&maxResults=%s".formatted(
			properties.playlistTracksUrl(),
			playlistId,
			properties.pageSize()
		);
		var tracks = new LinkedList<Track>();
		var nextToken = "";

		do {
			var response = execRequest(url, HttpMethod.GET, token, LinkedHashMap.class);
			var body = Objects.requireNonNull(response.getBody());
			tracks.addAll(trackMapper.mapList(body));

			nextToken = (String) body.get("nextPageToken");
			url += "&pageToken=" + nextToken;
		} while (nextToken != null);

		return tracks;
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
