package com.mf.auth.boot.config.properties;

import com.mf.auth.adapter.properties.MusicServiceProperties;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Qualifier("spotifyProperties")
public class SpotifyProperties implements MusicServiceProperties {

    @Value("${service.spotify.clientId}")
    private String clientId;

    @Value("${service.spotify.clientSecret}")
    private String clientSecret;

    @Value("${service.spotify.clientScope}")
    private String clientScope;

    @Value("${service.spotify.grantType}")
    private String grantType;

    @Value("${service.spotify.clientRedirectUrl}")
    private String redirectUrl;

    @Value("${service.spotify.backRedirectUrl}")
    private String backRedirectUrl;

    @Value("${service.spotify.urls.auth}")
    private String authUrl;

    @Value("${service.spotify.urls.token}")
    private String tokenUrl;

    @Override
    public String clientId() {
        return clientId;
    }

    @Override
    public String clientSecret() {
        return clientSecret;
    }

    @Override
    public String clientScope() {
        return clientScope;
    }

    @Override
    public String grantType() {
        return grantType;
    }

    @Override
    public String authUrl() {
        return authUrl;
    }

    @Override
    public String tokenUrl() {
        return tokenUrl;
    }

    @Override
    public String redirectUrl() {
        return redirectUrl;
    }

    @Override
    public String backRedirectUrl() {
        return backRedirectUrl;
    }
}
