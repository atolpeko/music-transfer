package com.mf.api.boot.config.properties;

import com.mf.api.adapter.out.musicservice.properties.SpotifyProperties;

import lombok.Setter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Setter
@Component
public class SpringSpotifyProperties implements SpotifyProperties {

    @Value("${service.spotify.name}")
    private String name;

    @Value("${service.spotify.urls.domain}")
    private String domain;

    @Value("${service.spotify.urls.likedTracks}")
    private String likedTracksUrl;

    @Value("${service.spotify.urls.likeTrack}")
    private String likeTrackUrl;

    @Value("${service.spotify.urls.searchTrack}")
    private String searchTrackUrl;

    @Value("${service.spotify.urls.playlists}")
    private String playlistsUrl;

    @Value("${service.spotify.urls.playlistTracks}")
    private String playlistTracks;

    @Value("${service.spotify.urls.me}")
    private String meUrl;

    @Value("${service.spotify.urls.createPlaylist}")
    private String createPlaylist;

    @Value("${service.spotify.pageSize}")
    private int pageSize;

    @Override
    public String name() {
        return name;
    }

    @Override
    public String domain() {
        return domain;
    }

    @Override
    public String likedTracksUrl() {
        return likedTracksUrl;
    }

    @Override
    public String trackLikeUrl() {
        return likeTrackUrl;
    }

    @Override
    public String searchTracksUrl() {
        return searchTrackUrl;
    }

    @Override
    public String playlistsUrl() {
        return playlistsUrl;
    }

    @Override
    public String playlistTracksUrl() {
        return playlistTracks;
    }

    @Override
    public String meUrl() {
        return meUrl;
    }

    @Override
    public String createPlaylistUrl() {
        return createPlaylist;
    }

    @Override
    public int pageSize() {
        return pageSize;
    }
}
