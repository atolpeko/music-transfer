package com.mf.auth.fixture;

public class AuthorizationFixture {

    public static final String AUTH_URL = "/api/auth/redirect";
    public static final String REDIRECT_URL = "http://host/some/url";

    public static final String SPOTIFY_CALLBACK_URL = "/api/auth/spotify/callback";
    public static final String SPOTIFY_LOGIN_REDIRECT = "/api/mock/spotify/authorize";
    public static final String SPOTIFY_TOKEN_VALUE = "mfknklmkvnkl";

    public static final String YT_MUSIC_CALLBACK_URL = "/api/auth/ytmusic/callback";
    public static final String YT_MUSIC_LOGIN_REDIRECT = "/api/mock/ytmusic/authorize";
    public static final String YT_MUSIC_TOKEN_VALUE = "mfknklmkvnkl";

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
