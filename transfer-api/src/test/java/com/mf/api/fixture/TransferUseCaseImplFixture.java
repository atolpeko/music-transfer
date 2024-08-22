package com.mf.api.fixture;

import com.mf.api.domain.entity.OAuth2Token;
import com.mf.api.domain.entity.Track;
import com.mf.api.usecase.entity.TransferRequest;
import com.mf.api.usecase.valueobject.TokenMap;

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

	public static final TransferRequest FULL_TRANSFER_REQUEST =
		TransferRequest.builder()
			.source(SOURCE)
			.target(TARGET)
			.tokenMap(TOKEN_MAP)
			.transferPlaylists(true)
			.build();

	public static final TransferRequest TRACK_TRANSFER_REQUEST =
		TransferRequest.builder()
			.source(SOURCE)
			.target(TARGET)
			.tokenMap(TOKEN_MAP)
			.transferPlaylists(false)
			.build();

	public static final TransferRequest REQUEST_INVALID_SOURCE_TARGET =
		TransferRequest.builder()
			.source(SOURCE)
			.target(SOURCE)
			.tokenMap(TOKEN_MAP)
			.transferPlaylists(true)
			.build();

	public static final TransferRequest REQUEST_INVALID_JWT =
		TransferRequest.builder()
			.source(SOURCE)
			.target(TARGET)
			.tokenMap(TOKEN_MAP)
			.transferPlaylists(true)
			.build();

	public static final List<Track> FAILED_TRACKS = List.of(
		Track.builder().serviceId("s1").name("t1").albumName("a1").artists(List.of("ar1")).build(),
		Track.builder().serviceId("s2").name("t2").albumName("a2").artists(List.of("ar2")).build()
	);

	public static final List<Track> FAILED_PLAYLIST_TRACKS = List.of(
		Track.builder().serviceId("s3").name("t3").albumName("a3").artists(List.of("ar3")).build(),
		Track.builder().serviceId("s4").name("t4").albumName("a4").artists(List.of("ar4")).build()
	);
}
