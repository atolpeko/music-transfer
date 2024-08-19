package com.mf.api.integration;

import static com.mf.api.adapter.in.rest.entity.MusicService.SPOTIFY;
import static com.mf.api.adapter.in.rest.entity.MusicService.YT_MUSIC;
import static com.mf.api.fixture.TransferFixture.INVALID_JWT;
import static com.mf.api.fixture.TransferFixture.MALFORMED_JWT;
import static com.mf.api.fixture.TransferFixture.URL;
import static com.mf.api.fixture.TransferFixture.VALID_JWT;
import static com.mf.api.fixture.TransferFixture.tokens;
import static com.mf.api.fixture.jsons.SpotifyJSONs.SPOTIFY_LIKED_TRACKS_NUMBER;
import static com.mf.api.fixture.jsons.SpotifyJSONs.SPOTIFY_PLAYLISTS_NUMBER;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.mf.api.config.IntegrationTest;
import com.mf.api.domain.service.JWTService;
import com.mf.api.usecase.entity.TransferResult;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@IntegrationTest
public class TransferTest {

	@Autowired
	MockMvc mvc;

	@Autowired
	ObjectMapper jsonMapper;

	@MockBean
	JWTService jwtService;

	@ParameterizedTest
	@MethodSource("provideArgumentsForTransferTest")
	void testFullTransfer(
		String source,
		String target,
		String token,
		boolean transferPlaylists,
		int transferredTracks,
		int transferredPlaylists,
		int failedToTransfer
	) throws Exception {
		when(jwtService.extractTokens(token.replace("Bearer ", "")))
			.thenReturn(tokens(source, target));

		var response = mvc.perform(post(URL)
				.param("source", source)
				.param("target", target)
				.param("transferPlaylists", String.valueOf(transferPlaylists))
				.header("Authorization", token))
			.andDo(print())
			.andExpect(status().is2xxSuccessful())
			.andReturn()
			.getResponse();

		var result = jsonMapper.readValue(response.getContentAsString(), TransferResult.class);
		assertEquals(result.getTransferredTracks(), transferredTracks);
		assertEquals(result.getFailedToTransfer().size(), failedToTransfer);
		assertEquals(result.getTransferredPlaylists(), transferredPlaylists);
	}

	static Stream<Arguments> provideArgumentsForTransferTest() {
		return Stream.of(
			Arguments.of(YT_MUSIC.name(), SPOTIFY.name(), VALID_JWT, true,
				SPOTIFY_LIKED_TRACKS_NUMBER, SPOTIFY_PLAYLISTS_NUMBER, 0),

			Arguments.of(YT_MUSIC.name(), SPOTIFY.name(), VALID_JWT, false,
				SPOTIFY_LIKED_TRACKS_NUMBER, 0, 0)
		);
	}

	@Test
	void testReturn400IfNoSourceProvided() throws Exception {
		mvc.perform(post(URL)
				.param("target", YT_MUSIC.name())
				.header("Authorization", "token"))
			.andDo(print())
			.andExpect(status().is(400));
	}

	@Test
	void testReturn400IfNoTargetProvided() throws Exception {
		mvc.perform(post(URL)
				.param("source", YT_MUSIC.name())
				.header("Authorization", "token"))
			.andDo(print())
			.andExpect(status().is(400));
	}

	@Test
	void testReturn400IfNoTokenProvided() throws Exception {
		mvc.perform(post(URL)
				.param("source", YT_MUSIC.name())
				.param("target", SPOTIFY.name()))
			.andDo(print())
			.andExpect(status().is(400));
	}

	@Test
	void testReturn401IfMalformedTokenProvided() throws Exception {
		mvc.perform(post(URL)
				.param("source", YT_MUSIC.name())
				.param("target", SPOTIFY.name())
				.header("Authorization", MALFORMED_JWT))
			.andDo(print())
			.andExpect(status().is(401));
	}

	@Test
	void testReturn401IfInvalidTokenProvided() throws Exception {
		mvc.perform(post(URL)
				.param("source", YT_MUSIC.name())
				.param("target", SPOTIFY.name())
				.header("Authorization", INVALID_JWT))
			.andDo(print())
			.andExpect(status().is(401));
	}
}
