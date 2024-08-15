package com.mf.api.fixture;

import com.mf.api.domain.entity.OAuth2Token;
import com.mf.api.domain.entity.Track;

import java.time.LocalDateTime;
import java.util.List;

public class TrackTransferExecutorFixture {

	public static final OAuth2Token TOKEN = OAuth2Token.builder()
		.value("dfdfd")
		.refreshToken("dfgdfhtrg")
		.expiresAt(LocalDateTime.MAX)
		.build();

	public static final List<Track> TRACKS = List.of(
		Track.builder().serviceId("s1").name("t1").albumName("a1").artists(List.of("ar1")).build(),
		Track.builder().serviceId("s2").name("t2").albumName("a2").artists(List.of("ar2")).build(),
		Track.builder().serviceId("s3").name("t4").albumName("a3").artists(List.of("ar3")).build(),
		Track.builder().serviceId("s3").name("t4").albumName("a4").artists(List.of("ar4")).build()
	);

	public static final List<Track> SOME_TRACKS = TRACKS.subList(2, 3);
}
