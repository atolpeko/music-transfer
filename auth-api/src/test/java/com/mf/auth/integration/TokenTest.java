package com.mf.auth.integration;

import static com.mf.auth.fixture.SpotifyAuthFixture.SPOTIFY_CODE;
import static com.mf.auth.fixture.TokenFixture.GET_JWT_URL;
import static com.mf.auth.fixture.TokenFixture.VALIDATE_URL;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mf.auth.adapter.in.rest.valueobject.MusicService;
import com.mf.auth.config.IntegrationTest;
import com.mf.auth.usecase.AuthUseCase;
import com.mf.auth.usecase.JWTUseCase;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@IntegrationTest
public class TokenTest {

	@Autowired
	MockMvc mvc;

	@Autowired
	AuthUseCase authUseCase;

	@Autowired
	JWTUseCase jwtUseCase;

	@Test
	void testObtainToken() throws Exception {
		var uuid = authUseCase.generateUuid().getValue();
		var token = authUseCase.auth(uuid, MusicService.SPOTIFY.name(), SPOTIFY_CODE);

		mvc.perform(get(GET_JWT_URL)
				.param("accessToken", token.getValue()))
			.andDo(print())
			.andExpect(status().is2xxSuccessful())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON));
	}

	@Test
	void testObtainTokenOnlyOnce() throws Exception {
		var uuid = authUseCase.generateUuid().getValue();
		var token = authUseCase.auth(uuid, MusicService.SPOTIFY.name(), SPOTIFY_CODE);
		jwtUseCase.obtain(token.getValue());

		mvc.perform(get(GET_JWT_URL)
				.param("accessToken", token.getValue()))
			.andDo(print())
			.andExpect(status().is(401));
	}

	@Test
	void testReturn401WhenAccessCodeIsInvalid() throws Exception {
		mvc.perform(get(GET_JWT_URL)
				.param("accessToken", "343434"))
			.andDo(print())
			.andExpect(status().is(401))
			.andExpect(content().contentType(MediaType.APPLICATION_JSON));
	}

	@Test
	void testTokenPositiveValidation() throws Exception {
		var uuid = authUseCase.generateUuid().getValue();
		var token = authUseCase.auth(uuid, MusicService.SPOTIFY.name(), SPOTIFY_CODE);
		var jwt = jwtUseCase.obtain(token.getValue());

		mvc.perform(get(VALIDATE_URL)
				.param("jwt", jwt.getValue()))
			.andDo(print())
			.andExpect(status().is2xxSuccessful());
	}

	@Test
	void testTokenNegativeValidation() throws Exception {
		mvc.perform(get(VALIDATE_URL)
				.param("jwt", "fddgfd"))
			.andDo(print())
			.andExpect(status().is(401));
	}
}
