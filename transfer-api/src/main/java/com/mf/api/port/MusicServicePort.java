package com.mf.api.port;

import com.mf.api.domain.entity.OAuth2Token;
import com.mf.api.port.exception.AccessException;
import com.mf.api.port.exception.MusicServiceException;
import com.mf.api.domain.entity.Playlist;
import com.mf.api.domain.entity.Track;
import com.mf.api.domain.valueobject.TrackSearchCriteria;
import com.mf.api.port.exception.NotSupportedException;

import java.util.List;
import java.util.Optional;

/**
 * Music service interface.
 */
public interface MusicServicePort {

	/**
	 * Fetch all liked tracks.
	 *
	 * @param token  OAuth2 access token
	 *
	 * @return a list of liked tracks
	 *
	 * @throws AccessException         if OAuth2 authorization fails
	 */
	List<Track> likedTracks(OAuth2Token token);

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
	 * @throws AccessException         if OAuth2 authorization fails
	 * @throws MusicServiceException   if any other music service related error occurs
	 * @throws NotSupportedException   may throw if this operation is not implemented
	 */
	Optional<Track> searchTracks(OAuth2Token token, TrackSearchCriteria criteria);

	/**
	 * Fetch all playlists.
	 *
	 * @param token  OAuth2 token
	 *
	 * @return a list of playlists
	 *
	 * @throws AccessException         if OAuth2 authorization fails
	 * @throws MusicServiceException   if any other music service related error occurs
	 */
	 List<Playlist> playlists(OAuth2Token token);

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
