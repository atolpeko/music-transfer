package com.mf.auth.config;

import static com.mf.auth.fixture.SpotifyAuthFixture.spotifyAuthCodeJson;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;

import com.mf.auth.adapter.properties.MusicServiceProperties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class WireMockConfig {

    @Autowired
    @Qualifier("spotifyProperties")
    MusicServiceProperties spotifyProperties;

    @Bean(initMethod = "start", destroyMethod = "stop")
    public WireMockServer wireMockServer() {
        var server = new WireMockServer(WireMockConfiguration.wireMockConfig().port(8089));
        mockSpotifyApi(server);

        return server;
    }

    private void mockSpotifyApi(WireMockServer server) {
        // Authentication code URL
        var authUrl = getUrl(spotifyProperties.authUrl());
        server.stubFor(WireMock.post(WireMock.urlEqualTo(authUrl))
            .willReturn(WireMock.aResponse()
                .withStatus(301)
                .withHeader("Location", spotifyProperties.redirectUrl())));

        // OAuth2 token exchange URL
        var tokenUrl = getUrl(spotifyProperties.tokenUrl());
        server.stubFor(WireMock.post(WireMock.urlEqualTo(tokenUrl))
            .willReturn(WireMock.aResponse()
                .withHeader("Content-Type", "application/json")
                .withBody(spotifyAuthCodeJson())));
    }

    private String getUrl(String url) {
        return url.replace("http://localhost:8089", "");
    }
}
