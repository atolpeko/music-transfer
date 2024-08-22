package com.mf.api.fixture;

import com.mf.api.domain.entity.OAuth2Token;
import com.mf.api.domain.entity.Playlist;
import com.mf.api.domain.entity.Track;
import com.mf.api.usecase.entity.TransferRequest;
import com.mf.api.usecase.valueobject.TokenMap;
import com.mf.api.util.Page;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class TransferUseCaseImplFixture {

	public static final String SOURCE = "src";
	public static final String TARGET = "target";

	public static final OAuth2Token SRC_TOKEN =
		OAuth2Token.builder()
			.value("dfdgfdgd")
			.refreshToken("dmvlkjns")
			.expiresAt(LocalDateTime.MAX)
			.build();

	public static final OAuth2Token TARGET_TOKEN =
		OAuth2Token.builder()
			.value("fddfgfg")
			.refreshToken("tgdasdtr")
			.expiresAt(LocalDateTime.MAX)
			.build();

	public static final Map<String, OAuth2Token> SERVICE_TO_TOKENS = Map.of(
		SOURCE, SRC_TOKEN,
		TARGET, TARGET_TOKEN
	);

	public static final TokenMap TOKEN_MAP = TokenMap.from(SERVICE_TO_TOKENS);

	public static final List<Track> TRACKS = List.of(
		Track.builder().serviceId("s1").name("t1").albumName("a1").artists(List.of("ar1")).build(),
		Track.builder().serviceId("s2").name("t2").albumName("a2").artists(List.of("ar2")).build(),
		Track.builder().serviceId("s3").name("t3").albumName("a3").artists(List.of("ar3")).build(),
		Track.builder().serviceId("s4").name("t4").albumName("a4").artists(List.of("ar4")).build()
	);

	public static final List<Track> SOME_TRACKS = TRACKS.subList(0, 2);

	public static final Playlist PLAYLIST_1 =
		Playlist.builder().id("i1").name("p1").tracks(TRACKS).build();

	public static final Playlist PLAYLIST_2 =
		Playlist.builder().id("i2").name("p2").tracks(TRACKS).build();

	public static final Playlist FAILED_PLAYLIST =
		Playlist.builder().id("i3").name("p3").tracks(SOME_TRACKS).build();

	public static final String NEXT = "NEXT";

	public static final Page<Track> TRACK_PAGE_1 = Page.of(TRACKS.subList(0, 2), NEXT);
	public static final Page<Track> TRACK_PAGE_2 = Page.of(TRACKS.subList(2, 4));
	public static final Page<Playlist> PLAYLIST_PAGE_1 = Page.of(List.of(PLAYLIST_1), NEXT);
	public static final Page<Playlist> PLAYLIST_PAGE_2 = Page.of(List.of(PLAYLIST_2));

	public static final TransferRequest<List<Track>> TRACK_TRANSFER_REQUEST =
		TransferRequest.<List<Track>>builder()
			.source(SOURCE)
			.target(TARGET)
			.tokenMap(TOKEN_MAP)
			.toTransfer(TRACKS)
			.build();

	public static final TransferRequest<Playlist> PLAYLIST_TRANSFER_REQUEST =
		TransferRequest.<Playlist>builder()
			.source(SOURCE)
			.target(TARGET)
			.tokenMap(TOKEN_MAP)
			.toTransfer(PLAYLIST_1)
			.build();
}
