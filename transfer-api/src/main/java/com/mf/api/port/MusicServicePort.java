package com.mf.api.port;

import com.mf.api.domain.entity.OAuth2Token;
import com.mf.api.port.exception.AccessException;
import com.mf.api.port.exception.MusicServiceException;
import com.mf.api.domain.entity.Playlist;
import com.mf.api.domain.entity.Track;
import com.mf.api.domain.valueobject.TrackSearchCriteria;
import com.mf.api.port.exception.IllegalRequestException;
import com.mf.api.port.exception.NotSupportedException;
import com.mf.api.util.Page;

import java.util.List;
import java.util.Optional;

/**
 * Music service interface.
 */
public interface MusicServicePort {

	/**
	 * Fetch a page of liked tracks.
	 *
	 * @param token  OAuth2 access token
	 * @param next   page URL
	 *
	 * @return a page of liked tracks
	 *
	 * @throws AccessException          if OAuth2 authorization fails
	 * @throws IllegalRequestException  if next is invalid
	 * @throws MusicServiceException    if any other music service related error occurs
	 */
	Page<Track> likedTracks(OAuth2Token token, String next);

	/**
	 * Fetch a page of playlist tracks.
	 *
	 * @param token       OAuth2 access token
	 * @param playlistId  playlist ID
	 * @param next        page URL
	 *
	 * @return a page of playlist tracks
	 *
	 * @throws AccessException          if OAuth2 authorization fails
	 * @throws IllegalRequestException  if next is invalid
	 * @throws MusicServiceException    if any other music service related error occurs
	 */
	Page<Track> playlistTracks(OAuth2Token token, String playlistId, String next);

	/**
	 * Like the specified track.
	 * Does nothing if the track is already liked.
	 *
	 * @param token  OAuth2 token
	 * @param track  track to like
	 *
	 * @throws AccessException         if OAuth2 authorization fails
	 * @throws MusicServiceException   if any other music service related error occurs
	 * @throws NotSupportedException   may throw if this operation is not implemented
	 */
	void likeTrack(OAuth2Token token, Track track);

	/**
	 * Like the specified tracks.
	 * Does nothing if these tracks are already liked.
	 *
	 * @param token   OAuth2 token
	 * @param tracks  tracks to like
	 *
	 * @throws AccessException         if OAuth2 authorization fails
	 * @throws MusicServiceException   if any other music service related error occurs
	 * @throws NotSupportedException   may throw if this operation is not implemented
	 */
	default void trackBulkLike(OAuth2Token token, List<Track> tracks) {
		tracks.forEach(track -> likeTrack(token, track));
	}

	/**
	 * Search for tracks using a search criteria.
	 *
	 * @param token     OAuth2 token
	 * @param criteria  search criteria to use
	 *
	 * @return a track if exists of Optional.empty()
	 *
	 * @throws AccessException           if OAuth2 authorization fails
	 * @throws IllegalArgumentException  if invalid criteria provided
	 * @throws MusicServiceException     if any other music service related error occurs
	 * @throws NotSupportedException     may throw if this operation is not implemented
	 */
	Optional<Track> searchTracks(OAuth2Token token, TrackSearchCriteria criteria);

	/**
	 * Fetch a page of playlists.
	 *
	 * @param token  OAuth2 token
	 * @param next   page URL
	 *
	 * @return a page of playlists
	 *
	 * @throws AccessException          if OAuth2 authorization fails
	 * @throws IllegalRequestException  if next is invalid
	 * @throws MusicServiceException    if any other music service related error occurs
	 */
	Page<Playlist> playlists(OAuth2Token token, String next);

	/**
	 * Create a new playlist.
	 *
	 * @param token         OAuth2 token
	 * @param playlist      playlist to create
	 *
	 * @return new playlist ID
	 *
	 * @throws AccessException         if OAuth2 authorization fails
	 * @throws MusicServiceException   if any other music service related error occurs
	 * @throws NotSupportedException   may throw if this operation is not implemented
	 */
	String createPlaylist(OAuth2Token token, Playlist playlist);

	/**
	 * Add the specified tracks to the playlist with the specified ID.
	 *
	 * @param token        OAuth2 token
	 * @param playlistId   ID of the playlist
	 * @param tracks       a list of tracks to add
	 *
	 * @throws AccessException         if OAuth2 authorization failss
	 * @throws MusicServiceException   if any other music service related error occurs
	 * @throws NotSupportedException   may throw if this operation is not implemented
	 */
	void addToPlaylist(OAuth2Token token, String playlistId, List<Track> tracks);
}
