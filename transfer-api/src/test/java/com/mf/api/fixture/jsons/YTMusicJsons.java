package com.mf.api.fixture.jsons;

import java.util.stream.IntStream;

public class YTMusicJsons {

	public static final int LIKED_TRACKS_NUMBER = 500;
	public static final int PLAYLISTS_NUMBER = 5;

	public static final String YT_MUSIC_TRACK_JSON =
		"""
		{
			"id": "%s",
			"snippet": {
				"title": "%s",
				"description": "%s",
				"channelTitle": "%s",
				"thumbnails": {
					"high": {
						"url": "%s"
					}
				}
			}
		}
		""";

	public static final String YT_MUSIC_LIKED_TRACKS_JSON =
		"""
		{
			"nextPageToken": null,
			"items": [
				%s
			]
		}
		""";

	public static final String YT_MUSIC_PLAYLIST_JSON =
		"""
		{
			"id": "%s",
			"snippet": {
				"title": "%s",
				"thumbnails": {
					"high": {
						"url": "%s"
					}
				}
			}
		}
		""";

	public static final String YT_MUSIC_PLAYLISTS_JSON =
		"""
		{
			"nextPageToken": null,
			"items": [
				%s
			]
		}
		""";

	public static String ytMusicLikedTracksJson() {
		var tracks = IntStream.range(0, LIKED_TRACKS_NUMBER)
			.mapToObj(i -> {
				var desc = "Provided to YouTube by ";
				return YT_MUSIC_TRACK_JSON
					.formatted(i, "name_" + i, desc, "artist_" + i, "img_" + i);
			})
			.reduce((t1, t2) -> String.join(",", t1, t2))
			.orElseThrow();

		return YT_MUSIC_LIKED_TRACKS_JSON.formatted(tracks);
	}

	public static String ytMusicPlaylistsJson() {
		var tracks = IntStream.range(0, PLAYLISTS_NUMBER)
			.mapToObj(i -> YT_MUSIC_PLAYLIST_JSON.formatted(i, "name_" + i, "img_" + i))
			.reduce((t1, t2) -> String.join(",", t1, t2))
			.orElseThrow();

		return YT_MUSIC_PLAYLISTS_JSON.formatted(tracks);
	}
}
