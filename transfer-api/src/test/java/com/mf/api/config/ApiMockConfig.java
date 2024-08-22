package com.mf.api.config;

import static com.mf.api.fixture.TransferFixture.INVALID_TOKEN;
import static com.mf.api.fixture.TransferFixture.JWT;
import static com.mf.api.fixture.TransferFixture.MALFORMED_TOKEN;
import static com.mf.api.fixture.jsons.SpotifyJSONs.SPOTIFY_CREATED_PLAYLIST_JSON;
import static com.mf.api.fixture.jsons.SpotifyJSONs.SPOTIFY_ME_JSON;
import static com.mf.api.fixture.jsons.SpotifyJSONs.SPOTIFY_PLAYLIST_ID;
import static com.mf.api.fixture.jsons.SpotifyJSONs.SPOTIFY_USER_ID;
import static com.mf.api.fixture.jsons.SpotifyJSONs.spotifyFoundTrackJson;
import static com.mf.api.fixture.jsons.SpotifyJSONs.spotifyLikedTracksJson;
import static com.mf.api.fixture.jsons.SpotifyJSONs.spotifyPlaylistsJson;
import static com.mf.api.fixture.jsons.YTMusicJsons.ytMusicLikedTracksJson;
import static com.mf.api.fixture.jsons.YTMusicJsons.ytMusicPlaylistsJson;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;

import com.mf.api.adapter.out.jwt.JwtValidatorProperties;
import com.mf.api.adapter.out.musicservice.properties.DefaultMusicServiceProperties;
import com.mf.api.adapter.out.musicservice.properties.SpotifyProperties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class ApiMockConfig {

    @Autowired
    public JwtValidatorProperties jwtValidatorProperties;

    @Autowired
    public SpotifyProperties spotifyProperties;

    @Autowired
    @Qualifier("ytMusicProperties")
    public DefaultMusicServiceProperties ytMusicProperties;

    @Bean(initMethod = "start", destroyMethod = "stop")
    public WireMockServer wireMockServer() {
        var server = new WireMockServer(WireMockConfiguration.options().dynamicPort());
        mockJwtValidatorApi(server);
        mockSpotifyApi(server);
        mockYTMusicApi(server);
        return server;
    }

    private void mockJwtValidatorApi(WireMockServer server) {

        // Valid JWT URL
        var jwtUrl = jwtValidatorProperties.jwtValidationUrl() + "?jwt=";
        server.stubFor(WireMock.get(WireMock.urlEqualTo(jwtUrl + JWT))
            .willReturn(WireMock.aResponse().withStatus(200)));

        // Invalid JWT URL
        var invalidJwtUrl = jwtUrl + INVALID_TOKEN;
        server.stubFor(WireMock.get(WireMock.urlEqualTo(invalidJwtUrl))
            .willReturn(WireMock.aResponse().withStatus(400)));

        // Malformed JWT URL
        var malformedJwtUrl = jwtUrl + MALFORMED_TOKEN;
        server.stubFor(WireMock.get(WireMock.urlEqualTo(malformedJwtUrl))
            .willReturn(WireMock.aResponse().withStatus(400)));
    }

    private void mockSpotifyApi(WireMockServer server) {

        // Liked tracks URL
        server.stubFor(WireMock.get(WireMock.urlPathEqualTo(spotifyProperties.likedTracksUrl()))
            .willReturn(WireMock.aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(spotifyLikedTracksJson())));

        // Search tracks URL
        server.stubFor(WireMock.get(WireMock.urlPathEqualTo(spotifyProperties.searchTracksUrl()))
            .willReturn(WireMock.aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(spotifyFoundTrackJson())));

        // Track like URL
        server.stubFor(WireMock.put(WireMock.urlPathEqualTo(spotifyProperties.trackLikeUrl()))
            .willReturn(WireMock.aResponse()
                .withStatus(201)));

        // Playlists URL
        server.stubFor(WireMock.get(WireMock.urlPathEqualTo(spotifyProperties.playlistsUrl()))
            .willReturn(WireMock.aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(spotifyPlaylistsJson())));

        // Create playlist URL
        var createPlaylistUrl = spotifyProperties.createPlaylistUrl()
            .replace("{user-id}", SPOTIFY_USER_ID);
        server.stubFor(WireMock.post(WireMock.urlMatching(createPlaylistUrl))
            .willReturn(WireMock.aResponse()
                .withStatus(201)
                .withHeader("Content-Type", "application/json")
                .withBody(SPOTIFY_CREATED_PLAYLIST_JSON)));

        // Playlist tracks URL
        var playlistTracksUrl = spotifyProperties.playlistTracksUrl()
            .replace("{id}", SPOTIFY_PLAYLIST_ID);
        server.stubFor(WireMock.get(WireMock.urlPathEqualTo(playlistTracksUrl))
            .willReturn(WireMock.aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(spotifyFoundTrackJson())));

        // Add to playlist URL
        server.stubFor(WireMock.post(WireMock.urlPathEqualTo(playlistTracksUrl))
            .willReturn(WireMock.aResponse()
                .withStatus(201)));

        // Me URL
        server.stubFor(WireMock.get(WireMock.urlPathEqualTo(spotifyProperties.meUrl()))
            .willReturn(WireMock.aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(SPOTIFY_ME_JSON)));
    }

    private void mockYTMusicApi(WireMockServer server) {

        // Liked tracks URL
        server.stubFor(WireMock.get(WireMock.urlPathEqualTo(ytMusicProperties.likedTracksUrl()))
            .willReturn(WireMock.aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(ytMusicLikedTracksJson())));

        // Playlists URL
        server.stubFor(WireMock.get(WireMock.urlPathEqualTo(ytMusicProperties.playlistsUrl()))
            .willReturn(WireMock.aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(ytMusicPlaylistsJson())));

        // Playlist tracks URL
        server.stubFor(WireMock.get(WireMock.urlPathEqualTo(ytMusicProperties.playlistTracksUrl()))
            .willReturn(WireMock.aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(ytMusicLikedTracksJson())));
    }
}
