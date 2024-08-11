package com.mf.auth.boot.config.properties;

import com.mf.auth.adapter.properties.MusicServiceProperties;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Qualifier("ytMusicProperties")
public class SpringYTMusicProperties implements MusicServiceProperties {

    @Value("${service.ytmusic.name}")
    private String name;

    @Value("${service.ytmusic.clientId}")
    private String clientId;

    @Value("${service.ytmusic.clientSecret}")
    private String clientSecret;

    @Value("${service.ytmusic.clientScope}")
    private String clientScope;

    @Value("${service.ytmusic.grantType}")
    private String grantType;

    @Value("${service.ytmusic.clientRedirectUrl}")
    private String redirectUrl;

    @Value("${service.ytmusic.urls.auth}")
    private String authUrl;

    @Value("${service.ytmusic.urls.token}")
    private String tokenUrl;

    @Override
    public String name() {
        return name;
    }

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
}
