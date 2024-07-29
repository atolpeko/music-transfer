package com.mf.auth.fixture;

public class SpotifyAuthFixture {
    public static final String SPOTIFY_CODE = "gkrnjkrlm43rfrfw465";

    public static final String AUTH_URL = "/api/auth/spotify/redirect";
    public static final String CALLBACK_URL = "/api/auth/spotify/callback";
    public static final String TOKEN_VALUE = "mfknklmkvnkl";

    public static String spotifyAuthCodeJson() {
        return "{\"access_token\":\"" + TOKEN_VALUE
            + "\",\"token_type\":\"Bearer\""
            + ",\"expires_in\":600}";
    }
}
