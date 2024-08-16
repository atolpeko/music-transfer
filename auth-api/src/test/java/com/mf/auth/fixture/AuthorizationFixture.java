package com.mf.auth.fixture;

public class AuthorizationFixture {
    public static final String SPOTIFY_AUTH_URL = "/api/auth/spotify/redirect";
    public static final String SPOTIFY_CALLBACK_URL = "/api/auth/spotify/callback";
    public static final String SPOTIFY_LOGIN_REDIRECT = "http://localhost:8089/api/mock/spotify/authorize";
    public static final String SPOTIFY_TOKEN_VALUE = "mfknklmkvnkl";

    public static final String YT_MUSIC_AUTH_URL = "/api/auth/ytmusic/redirect";
    public static final String YT_MUSIC_CALLBACK_URL = "/api/auth/ytmusic/callback";
    public static final String YT_MUSIC_LOGIN_REDIRECT = "http://localhost:8089/api/mock/ytmusic/authorize";
    public static final String YT_MUSIC_TOKEN_VALUE = "mfknklmkvnkl";

    public static final String REDIRECT_URL = "http://localhost:5980/";

    public static final String SPOTIFY_CODE = "gkrnjkrlm43rfrfw465";
    public static final String YT_MUSIC_CODE = "gkrnjkrlm43rfrfw465";

    public static String ytMusicAuthCodeJson() {
        return "{\"access_token\":\"" + SPOTIFY_TOKEN_VALUE
            + "\",\"token_type\":\"Bearer\""
            + ",\"expires_in\":600}";
    }

    public static String spotifyAuthCodeJson() {
        return "{\"access_token\":\"" + YT_MUSIC_TOKEN_VALUE
            + "\",\"token_type\":\"Bearer\""
            + ",\"expires_in\":600}";
    }
}
