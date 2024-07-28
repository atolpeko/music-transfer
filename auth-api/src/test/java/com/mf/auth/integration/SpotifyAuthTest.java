package com.mf.auth.integration;

import com.mf.auth.adapter.in.rest.RestProperties;
import com.mf.auth.adapter.in.rest.valueobject.MusicService;
import com.mf.auth.adapter.properties.MusicServiceProperties;
import com.mf.auth.config.IntegrationTest;
import com.mf.auth.adapter.in.rest.service.EncodeStateService;
import com.mf.auth.domain.service.SymmetricEncryptionService;
import com.mf.auth.usecase.AuthUseCase;
import com.mf.auth.usecase.JWTUseCase;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.web.servlet.MockMvc;

import static com.mf.auth.fixture.SpotifyAuthFixture.AUTH_URL;
import static com.mf.auth.fixture.SpotifyAuthFixture.CALLBACK_URL;
import static com.mf.auth.fixture.SpotifyAuthFixture.SPOTIFY_CODE;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTest
class SpotifyAuthTest {

	@Autowired
	MockMvc mvc;

	@Autowired
	AuthUseCase authUseCase;

	@Autowired
	JWTUseCase jwtUseCase;

	@Autowired
	EncodeStateService encodeStateService;

	@Autowired
	SymmetricEncryptionService encryptionService;

	@Autowired
	@Qualifier("spotifyProperties")
	MusicServiceProperties properties;

	@Autowired
	RestProperties restProperties;

	@Test
	void testRedirectionToSpotifyOnAuthCall() throws Exception {
		var redirect = performAuthRedirect();
		var encodedState = redirect.substring(redirect.indexOf("&state=") + 7);
		var state = encodeStateService.decode(encodedState);

		assertTrue(redirect.startsWith(properties.authUrl()));
		assertNotNull(state);
		assertNotNull(state.getUuid());
		assertNotNull(state.getRedirectUrl());
	}

	private String performAuthRedirect() throws Exception {
		return mvc.perform(get(AUTH_URL))
			.andDo(print())
			.andExpect(status().is3xxRedirection())
			.andReturn()
			.getResponse()
			.getRedirectedUrl();
	}

	@Test
	void testRedirectionToSpotifyAuthOnAuthWithJwtCall() throws Exception {
		var prevUuid = authUseCase.generateUuid().getValue();
		var prevToken = authUseCase.auth(prevUuid, MusicService.SPOTIFY.name(), SPOTIFY_CODE);
		var prevJwt = jwtUseCase.obtain(prevToken.getValue()).getValue();

		var redirect = mvc.perform(get(AUTH_URL)
				.param("jwt", prevJwt))
			.andDo(print())
			.andExpect(status().is3xxRedirection())
			.andReturn()
			.getResponse()
			.getRedirectedUrl();

		var encodedState = redirect.substring(redirect.indexOf("&state=") + 7);
		var state = encodeStateService.decode(encodedState);
		var uuid = encryptionService.decrypt(state.getUuid(),
			restProperties.uuidSecret());
		var jwt = encryptionService.decrypt(state.getJwt(), uuid);

		assertTrue(redirect.startsWith(properties.authUrl()));
		assertNotNull(state);
		assertNotNull(jwt);
		assertNotNull(state.getRedirectUrl());
	}

	@Test
	void testReturn401IfInvalidJwtProvided() throws Exception {
		mvc.perform(get(AUTH_URL)
				.param("jwt", "335336"))
			.andDo(print())
			.andExpect(status().is(401));
	}

	@Test
	void testRedirectToClientWithAccessTokenOnCallbackCall() throws Exception {
		var callback = performAuthRedirect();
		var state = callback.substring(callback.indexOf("&state=") + 7);

		var redirect = mvc.perform(get(CALLBACK_URL)
				.param("code", SPOTIFY_CODE)
				.param("state", state))
			.andDo(print())
			.andExpect(status().is3xxRedirection())
			.andReturn()
			.getResponse()
			.getRedirectedUrl();

		var accessToken = redirect.substring(redirect.indexOf("?access_token=") + 14);
		var jwt = jwtUseCase.obtain(accessToken);
		assertTrue(jwt.isValid());
	}

	@Test
	void testReturn403IfErrorProvided() throws Exception {
		var callback = performAuthRedirect();
		var state = encodeStateService.decode(
			callback.substring(callback.indexOf("&state=") + 7)
		);

		mvc.perform(get(CALLBACK_URL)
				.param("error", "ERROR")
				.param("state", encodeStateService.encode(state)))
			.andDo(print())
			.andExpect(status().is(403));
	}

	@Test
	void testReturn400IfInvalidUuidProvided() throws Exception {
		var callback = performAuthRedirect();
		var state = encodeStateService.decode(
			callback.substring(callback.indexOf("&state=") + 7)
		);
		state.setUuid(
			encryptionService.encrypt("mkhjklmknj", restProperties.uuidSecret())
		);

		mvc.perform(get(CALLBACK_URL)
				.param("code", SPOTIFY_CODE)
				.param("state", encodeStateService.encode(state)))
			.andDo(print())
			.andExpect(status().is(401));
	}

	@Test
	void testReturn400IfInvalidJwtProvided() throws Exception {
		var callback = performAuthRedirect();
		var state = encodeStateService.decode(
			callback.substring(callback.indexOf("&state=") + 7)
		);
		var uuid = encryptionService.decrypt(state.getUuid(),
			restProperties.uuidSecret());
		state.setJwt(
			encryptionService.encrypt("mkhjklmklfkemlnj", uuid)
		);

		mvc.perform(get(CALLBACK_URL)
				.param("code", SPOTIFY_CODE)
				.param("state", encodeStateService.encode(state)))
			.andDo(print())
			.andExpect(status().is(401));
	}
}
