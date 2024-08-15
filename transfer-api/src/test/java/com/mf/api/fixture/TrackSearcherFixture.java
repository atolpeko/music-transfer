package com.mf.api.fixture;

import com.mf.api.domain.entity.OAuth2Token;
import com.mf.api.domain.entity.Track;
import com.mf.api.domain.valueobject.TrackSearchCriteria;

import java.time.LocalDateTime;
import java.util.List;

public class TrackSearcherFixture {

	public static final OAuth2Token TOKEN = OAuth2Token.builder()
		.value("dfdfd")
		.refreshToken("dfgdfhtrg")
		.expiresAt(LocalDateTime.MAX)
		.build();

	public static final List<Track> TRACKS = List.of(
		Track.builder().serviceId("s1").name("t1").albumName("a1").artists(List.of("ar1")).build(),
		Track.builder().serviceId("s2").name("t2").albumName("a2").artists(List.of("ar2")).build()
	);

	public static final TrackSearchCriteria FULL_CRITERIA_1 =
		TrackSearchCriteria.builder()
			.trackName("t1")
			.albumName("a1")
			.artists(List.of("ar1"))
			.build();

	public static final TrackSearchCriteria FULL_CRITERIA_2 =
		TrackSearchCriteria.builder()
			.trackName("t2")
			.albumName("a2")
			.artists(List.of("ar2"))
			.build();

	public static final TrackSearchCriteria BY_NAME_1 =
		TrackSearchCriteria.builder()
			.trackName("t1")
			.build();

	public static final TrackSearchCriteria BY_NAME_2 =
		TrackSearchCriteria.builder()
			.trackName("t2")
			.build();

	public static final TrackSearchCriteria BY_NAME_AND_ARTIST_1 =
		TrackSearchCriteria.builder()
			.trackName("t1")
			.artists(List.of("ar1"))
			.build();

	public static final TrackSearchCriteria BY_NAME_AND_ARTIST_2 =
		TrackSearchCriteria.builder()
			.trackName("t2")
			.artists(List.of("ar2"))
			.build();
}
