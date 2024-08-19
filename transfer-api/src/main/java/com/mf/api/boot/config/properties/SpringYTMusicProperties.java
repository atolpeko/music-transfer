package com.mf.api.boot.config.properties;

import com.mf.api.adapter.out.musicservice.properties.DefaultMusicServiceProperties;

import lombok.Setter;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Setter
@Component
@Qualifier("ytMusicProperties")
public class SpringYTMusicProperties implements DefaultMusicServiceProperties {

    @Value("${service.ytmusic.name}")
    private String name;

    @Value("${service.ytmusic.urls.domain}")
    private String domain;

    @Value("${service.ytmusic.urls.likedTracks}")
    private String likedTracksUrl;

    @Value("${service.ytmusic.urls.playlists}")
    private String playlistsUrl;

    @Value("${service.ytmusic.urls.playlistTracks}")
    private String playlistTracksUrl;

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
    public String playlistsUrl() {
        return playlistsUrl;
    }

    @Override
    public String playlistTracksUrl() {
        return playlistTracksUrl;
    }

    @Override
    public int pageSize() {
        return pageSize;
    }

    @Override
    public String trackLikeUrl() {
        return null;
    }

    @Override
    public String searchTracksUrl() {
        return null;
    }
}
