package com.mf.api.integration;

import static com.mf.api.adapter.in.rest.entity.MusicService.SPOTIFY;
import static com.mf.api.adapter.in.rest.entity.MusicService.YT_MUSIC;
import static com.mf.api.fixture.TransferFixture.INVALID_TOKEN;
import static com.mf.api.fixture.TransferFixture.INVALID_PLAYLIST_JSON_1;
import static com.mf.api.fixture.TransferFixture.INVALID_PLAYLIST_JSON_2;
import static com.mf.api.fixture.TransferFixture.INVALID_TRACK_JSON_1;
import static com.mf.api.fixture.TransferFixture.INVALID_TRACK_JSON_2;
import static com.mf.api.fixture.TransferFixture.INVALID_TRACK_JSON_3;
import static com.mf.api.fixture.TransferFixture.INVALID_TRACK_JSON_4;
import static com.mf.api.fixture.TransferFixture.INVALID_TRACK_JSON_5;
import static com.mf.api.fixture.TransferFixture.JWT;
import static com.mf.api.fixture.TransferFixture.MALFORMED_TOKEN;
import static com.mf.api.fixture.TransferFixture.PLAYLISTS_URL;
import static com.mf.api.fixture.TransferFixture.TRACKS_URL;
import static com.mf.api.fixture.TransferFixture.VALID_TOKEN;
import static com.mf.api.fixture.TransferFixture.noTracksJson;
import static com.mf.api.fixture.TransferFixture.playlistJson;
import static com.mf.api.fixture.TransferFixture.tokens;
import static com.mf.api.fixture.TransferFixture.tooManyTracksJson;
import static com.mf.api.fixture.TransferFixture.tracksJson;

import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mf.api.config.IntegrationTest;
import com.mf.api.domain.service.JWTService;

import java.util.Collections;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@IntegrationTest
class TransferTest {

	@Autowired
	MockMvc mvc;

	@MockBean
	JWTService jwtService;

	@ParameterizedTest
	@MethodSource("provideArgumentsForTrackTransfer")
	void testTransfer(
		String url,
		String source,
		String target,
		String token,
		String jwt,
		String json
	) throws Exception {
		when(jwtService.extractTokens(jwt)).thenReturn(tokens(source, target));
		mvc.perform(post(url)
				.param("source", source)
				.param("target", target)
				.content(json)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", token))
			.andDo(print())
			.andExpect(status().is2xxSuccessful())
			.andReturn()
			.getResponse();
	}

	static Stream<Arguments> provideArgumentsForTrackTransfer() {
		return Stream.of(
			Arguments.of(TRACKS_URL, YT_MUSIC.name(), SPOTIFY.name(),
				VALID_TOKEN, JWT, tracksJson()),
			Arguments.of(PLAYLISTS_URL, YT_MUSIC.name(), SPOTIFY.name(),
				VALID_TOKEN, JWT, playlistJson())
		);
	}

	@ParameterizedTest
	@MethodSource("provideArgumentsForValidationTests")
	void testReturn400IfNoSourceProvided(
		String url,
		String json,
		String service
	) throws Exception {
		mvc.perform(post(url)
				.param("target", service)
				.content(json)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", VALID_TOKEN))
			.andDo(print())
			.andExpect(status().is(400));
	}

	static Stream<Arguments> provideArgumentsForValidationTests() {
		return Stream.of(
			Arguments.of(TRACKS_URL, tracksJson(), YT_MUSIC.name(), SPOTIFY.name()),
			Arguments.of(TRACKS_URL, tracksJson(), SPOTIFY.name(), YT_MUSIC.name()),
			Arguments.of(PLAYLISTS_URL, playlistJson(), YT_MUSIC.name(), SPOTIFY.name()),
			Arguments.of(PLAYLISTS_URL, playlistJson(), SPOTIFY.name(), YT_MUSIC.name())
		);
	}

	@ParameterizedTest
	@MethodSource("provideArgumentsForValidationTests")
	void testReturn400IfNoTargetProvided(
		String url,
		String json,
		String service
	) throws Exception {
		mvc.perform(post(url)
				.param("source", service)
				.content(json)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", VALID_TOKEN))
			.andDo(print())
			.andExpect(status().is(400));
	}

	@ParameterizedTest
	@MethodSource("provideArgumentsForValidationTests")
	void testReturn400IfNoJsonProvided(
		String url,
		String json,
		String sourceService,
		String targetService
	) throws Exception {
		mvc.perform(post(url)
				.param("source", sourceService)
				.param("target", targetService)
				.header("Authorization", VALID_TOKEN))
			.andDo(print())
			.andExpect(status().is(400));
	}

	@ParameterizedTest
	@MethodSource("provideArgumentsForTrackValidationTests")
	void testReturns400IfInvalidTracksProvided(
		String url,
		String source,
		String target,
		String token,
		String jwt,
		String json
	) throws Exception {
		when(jwtService.extractTokens(jwt)).thenReturn(tokens(source, target));
		mvc.perform(post(url)
				.param("source", source)
				.param("target", target)
				.content(json)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", token))
			.andDo(print())
			.andExpect(status().is4xxClientError())
			.andReturn()
			.getResponse();
	}

	static Stream<Arguments> provideArgumentsForTrackValidationTests() {
		return Stream.of(
			Arguments.of(TRACKS_URL, YT_MUSIC.name(), SPOTIFY.name(),
				VALID_TOKEN, JWT, noTracksJson()),
			Arguments.of(TRACKS_URL, YT_MUSIC.name(), SPOTIFY.name(),
				VALID_TOKEN, JWT, tooManyTracksJson())
		);
	}

	@ParameterizedTest
	@MethodSource("provideArgumentsForJsonValidationTests")
	void testReturn400IfInvalidJsonProvided(
		String url,
		String json,
		String sourceService,
		String targetService
	) throws Exception {
		when(jwtService.extractTokens(JWT)).thenReturn(tokens(sourceService, targetService));
		mvc.perform(post(url)
				.param("source", sourceService)
				.param("target", targetService)
				.content(json)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", VALID_TOKEN))
			.andDo(print())
			.andExpect(status().is(400));
	}

	static Stream<Arguments> provideArgumentsForJsonValidationTests() {
		return Stream.of(
			Arguments.of(TRACKS_URL, INVALID_TRACK_JSON_1, YT_MUSIC.name(), SPOTIFY.name()),
			Arguments.of(TRACKS_URL, INVALID_TRACK_JSON_2, YT_MUSIC.name(), SPOTIFY.name()),
			Arguments.of(TRACKS_URL, INVALID_TRACK_JSON_3, YT_MUSIC.name(), SPOTIFY.name()),
			Arguments.of(TRACKS_URL, INVALID_TRACK_JSON_4, YT_MUSIC.name(), SPOTIFY.name()),
			Arguments.of(TRACKS_URL, INVALID_TRACK_JSON_5, YT_MUSIC.name(), SPOTIFY.name()),
			Arguments.of(PLAYLISTS_URL, INVALID_PLAYLIST_JSON_1, YT_MUSIC.name(), SPOTIFY.name()),
			Arguments.of(PLAYLISTS_URL, INVALID_PLAYLIST_JSON_2, YT_MUSIC.name(), SPOTIFY.name())
		);
	}

	@ParameterizedTest
	@MethodSource("provideArgumentsForValidationTests")
	void testReturn400IfMalformedJsonProvided(
		String url,
		String json,
		String sourceService,
		String targetService
	) throws Exception {
		when(jwtService.extractTokens(JWT)).thenReturn(tokens(sourceService, targetService));
		mvc.perform(post(url)
				.param("source", sourceService)
				.param("target", targetService)
				.content("{n}")
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", VALID_TOKEN))
			.andDo(print())
			.andExpect(status().is(400));
	}

	@ParameterizedTest
	@MethodSource("provideArgumentsForValidationTests")
	void testReturn400IfNoTokenProvided(
		String url,
		String json,
		String sourceService,
		String targetService
	) throws Exception {
		mvc.perform(post(url)
				.param("source", sourceService)
				.param("target", targetService)
				.content(json)
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().is(400));
	}

	@ParameterizedTest
	@MethodSource("provideArgumentsForValidationTests")
	void testReturn401IfMalformedTokenProvided(
		String url,
		String json,
		String sourceService,
		String targetService
	) throws Exception {
		mvc.perform(post(url)
				.param("source", sourceService)
				.param("target", targetService)
				.content(json)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", MALFORMED_TOKEN))
			.andDo(print())
			.andExpect(status().is(401));
	}

	@ParameterizedTest
	@MethodSource("provideArgumentsForValidationTests")
	void testReturn401IfInvalidTokenProvided(
		String url,
		String json,
		String sourceService,
		String targetService
	) throws Exception {
		mvc.perform(post(url)
				.param("source", sourceService)
				.param("target", targetService)
				.content(json)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", INVALID_TOKEN))
			.andDo(print())
			.andExpect(status().is(401));
	}
}
