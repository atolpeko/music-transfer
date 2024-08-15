package com.mf.api.fixture;

import com.mf.api.domain.entity.OAuth2Token;
import com.mf.api.domain.entity.Playlist;
import com.mf.api.domain.entity.Track;

import java.time.LocalDateTime;
import java.util.List;

public class PlaylistTransferExecutorFixture {

	public static final OAuth2Token TOKEN = OAuth2Token.builder()
		.value("dfdfd")
		.refreshToken("dfgdfhtrg")
		.expiresAt(LocalDateTime.MAX)
		.build();

	public static final List<Track> TRACKS_1 = List.of(
		Track.builder().serviceId("s1").name("t1").albumName("a1").artists(List.of("ar1")).build(),
		Track.builder().serviceId("s2").name("t2").albumName("a2").artists(List.of("ar2")).build()
	);

	public static final List<Track> TRACKS_2 = List.of(
		Track.builder().serviceId("s3").name("t3").albumName("a3").artists(List.of("ar3")).build(),
		Track.builder().serviceId("s4").name("t4").albumName("a4").artists(List.of("ar4")).build()
	);

	public static final List<Track> SOME_TRACKS_1 = TRACKS_1.subList(0, 1);
	public static final List<Track> SOME_TRACKS_2 = TRACKS_2.subList(0, 1);

	public static final List<Playlist> PLAYLISTS = List.of(
		Playlist.builder().id("i1").name("p1").tracks(TRACKS_1).build(),
		Playlist.builder().id("i2").name("p2").tracks(TRACKS_2).build()
	);
}
