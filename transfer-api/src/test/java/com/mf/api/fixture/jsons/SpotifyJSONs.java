package com.mf.api.fixture.jsons;

import java.util.UUID;
import java.util.stream.IntStream;

public class SpotifyJSONs {

	public static final int SPOTIFY_LIKED_TRACKS_NUMBER = 500;
	public static final int SPOTIFY_PLAYLISTS_NUMBER = 5;

	public static final String SPOTIFY_USER_ID = "343434";
	public static final String SPOTIFY_PLAYLIST_ID = "45rg86fs";

	public static final String SPOTIFY_TRACK_JSON =
        """
        {
            "track": {
                "id": "%s",
                "name": "%s",
                "album": {
                    "name": "%s",
                    "images": [
                        {
                            "url": "%s"
                        }
                    ]
                },
                "artists": [
                    {
                        "name": "%s"
                    }
                ]
            }
        }
        """;

	public static final String SPOTIFY_LIKED_TRACKS_JSON =
		"""
		{
			"next": null,
			"items": [
				%s
			]
		}
		""";

	public static final String SPOTIFY_FOUND_TRACKS_JSON =
		"""
		{
			"next": null,
			"tracks": {
				"items": [
					%s
				]
			}
		}
		""";

	public static final String SPOTIFY_PLAYLIST_JSON =
		"""
		{
			"id": "%s",
			"name": "%s",
			"images": [
				{
					"url": "%s"
				}
			]
		}
		""";

	public static final String SPOTIFY_PLAYLISTS_JSON =
		"""
		{
			"next": null,
			"items": [
				%s
			]
		}
		""";

	public static final String SPOTIFY_CREATED_PLAYLIST_JSON =
		"""
		{
			"id": "%s",
			"name": "name"
		}
		""".formatted(SPOTIFY_PLAYLIST_ID);

	public static final String SPOTIFY_ME_JSON =
		"""
		{
			"id": "%s"
		}
		""".formatted(SPOTIFY_USER_ID);

	public static String spotifyLikedTracksJson() {
		var tracks = IntStream.range(0, SPOTIFY_LIKED_TRACKS_NUMBER)
			.mapToObj(i -> SPOTIFY_TRACK_JSON
				.formatted(i, "name_" + i, "album_" + i, "img_" + i, "artist_" + i))
			.reduce((t1, t2) -> String.join(",", t1, t2))
			.orElseThrow();

		return SPOTIFY_LIKED_TRACKS_JSON.formatted(tracks);
	}

	public static String spotifyFoundTrackJson() {
		var i = UUID.randomUUID();
		var track = SPOTIFY_TRACK_JSON
			.formatted(i, "name_" + i, "album_" + i, "img_" + i, "artist_" + i);
		return SPOTIFY_FOUND_TRACKS_JSON.formatted(track);
	}

	public static String spotifyPlaylistsJson() {
		var lists = IntStream.range(0, SPOTIFY_PLAYLISTS_NUMBER)
			.mapToObj(i -> SPOTIFY_PLAYLIST_JSON.formatted(i, "name" + i, "img" + i))
			.reduce((t1, t2) -> String.join(",", t1, t2))
			.orElseThrow();

		return SPOTIFY_PLAYLISTS_JSON.formatted(lists);
	}
}
