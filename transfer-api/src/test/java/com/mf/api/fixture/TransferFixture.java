package com.mf.api.fixture;

import com.mf.api.domain.entity.OAuth2Token;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.IntStream;

public class TransferFixture {

    public static final String TRACKS_URL = "/api/transfer/tracks";
    public static final String PLAYLISTS_URL = "/api/transfer/playlists";

    public static final String VALID_TOKEN = "Bearer vdffelkjnelpdkhdiofplkd";
    public static final String JWT = "vdffelkjnelpdkhdiofplkd";
    public static final String INVALID_TOKEN = "gdgd";
    public static final String MALFORMED_TOKEN = "grtrgr";

    public static final String PLAYLIST_JSON = """
        {
            "id": "id",
            "name": "Playlist",
            "tracks": [ %s ]
        }
        """;

    public static final String TRACK_JSON = """
        {
            "id": "%s",
            "name": "%s",
            "albumName": "%s",
            "artists": [ "%s" ]
        }
        """;

    public static final String INVALID_TRACK_JSON_1 = "{}";
    public static final String INVALID_TRACK_JSON_2 = """
        {
            "name": "Name"
        }
        """;
    public static final String INVALID_TRACK_JSON_3 = """
        {
            "name": ""
        }
        """;
    public static final String INVALID_TRACK_JSON_4 = """
        {
            "albumName": "Name"
        }
        """;
    public static final String INVALID_TRACK_JSON_5 = """
        {
            "albumName": ""
        }
        """;

    public static final String INVALID_PLAYLIST_JSON_1 = "{}";
    public static final String INVALID_PLAYLIST_JSON_2 = """
        {
            "name": ""
        }
        """;

    public static Map<String, OAuth2Token> tokens(String source, String target) {
        return Map.of(
            source, new OAuth2Token("fdfddf", "mklfnl", LocalDateTime.MAX),
            target, new OAuth2Token("kmoere", "lkmeff", LocalDateTime.MAX)
        );
    }

    public static String tracksJson() {
         return """
             {
                "tracks": [ %s ]
             }
             """.formatted(tracks(10));
    }

    public static String tooManyTracksJson() {
        return """
             {
                "tracks": [ %s ]
             }
             """.formatted(tracks(50));
    }

    public static String noTracksJson() {
        return """
             {
                "tracks": [ ]
             }
             """;
    }

    private static String tracks(int count) {
        return IntStream.range(0, count)
            .mapToObj(i -> TRACK_JSON.formatted("id" + i, "n" + i, "a" + i, "ar" + i))
            .reduce((s1, s2) -> String.join(", ", s1, s2))
            .orElseThrow();
    }

    public static String playlistJson() {
        return PLAYLIST_JSON.formatted(tracks(10));
    }
}
