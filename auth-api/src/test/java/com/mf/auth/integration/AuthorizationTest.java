package com.mf.auth.integration;

import static com.mf.auth.fixture.AuthorizationFixture.SPOTIFY_AUTH_URL;
import static com.mf.auth.fixture.AuthorizationFixture.SPOTIFY_CALLBACK_URL;
import static com.mf.auth.fixture.AuthorizationFixture.SPOTIFY_LOGIN_REDIRECT;
import static com.mf.auth.fixture.AuthorizationFixture.YT_MUSIC_AUTH_URL;
import static com.mf.auth.fixture.AuthorizationFixture.YT_MUSIC_CALLBACK_URL;
import static com.mf.auth.fixture.AuthorizationFixture.YT_MUSIC_CODE;
import static com.mf.auth.fixture.AuthorizationFixture.REDIRECT_URL;
import static com.mf.auth.fixture.AuthorizationFixture.SPOTIFY_CODE;

import static com.mf.auth.fixture.AuthorizationFixture.YT_MUSIC_LOGIN_REDIRECT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mf.auth.adapter.in.rest.RestProperties;
import com.mf.auth.adapter.in.rest.service.EncodeStateService;
import com.mf.auth.adapter.in.rest.valueobject.MusicService;
import com.mf.auth.config.IntegrationTest;
import com.mf.auth.domain.service.SymmetricEncryptionService;
import com.mf.auth.usecase.AuthUseCase;
import com.mf.auth.usecase.JWTUseCase;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

@IntegrationTest
class AuthorizationTest {

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
	RestProperties restProperties;

	@ParameterizedTest
	@MethodSource("provideArgumentsForAuthTest")
	void testRedirectionToMusicServiceLoginOnAuthCall(
		String authUrl,
		String redirectUrl,
		String returnedAuthUrl
	) throws Exception {
		var redirect = performAuthRedirect(authUrl, redirectUrl);
		var encodedState = redirect.substring(redirect.indexOf("&state=") + 7);
		var state = encodeStateService.decode(encodedState);

		assertTrue(redirect.startsWith(returnedAuthUrl));
		assertNotNull(state);
		assertNotNull(state.getUuid());
		assertEquals(redirectUrl, state.getRedirectUrl());
	}

	private String performAuthRedirect(
		String url,
		String redirectUrl
	) throws Exception {
		return mvc.perform(get(url)
				.param("redirectUrl", redirectUrl))
			.andDo(print())
			.andExpect(status().is3xxRedirection())
			.andReturn()
			.getResponse()
			.getRedirectedUrl();
	}

	private static Stream<Arguments> provideArgumentsForAuthTest() {
		return Stream.of(
			Arguments.of(SPOTIFY_AUTH_URL, REDIRECT_URL, SPOTIFY_LOGIN_REDIRECT),
			Arguments.of(YT_MUSIC_AUTH_URL, REDIRECT_URL, YT_MUSIC_LOGIN_REDIRECT)
		);
	}

	@ParameterizedTest
	@MethodSource("provideArgumentsForAuthWithJwtTest")
	void testRedirectionToMusicServiceLoginOnAuthWithJwtCall(
		String authUrl,
		String redirectUrl,
		String returnedAuthUrl,
		MusicService service,
		String authCode
	) throws Exception {
		var prevUuid = authUseCase.generateUuid().getValue();
		var prevToken = authUseCase.auth(prevUuid,service.name(), authCode);
		var prevJwt = jwtUseCase.obtain(prevToken.getValue()).getValue();

		var redirect = mvc.perform(get(authUrl)
				.param("redirectUrl", redirectUrl)
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

		assertTrue(redirect.startsWith(returnedAuthUrl));
		assertNotNull(state);
		assertNotNull(jwt);
		assertEquals(redirectUrl, state.getRedirectUrl());
	}

	private static Stream<Arguments> provideArgumentsForAuthWithJwtTest() {
		return Stream.of(
			Arguments.of(SPOTIFY_AUTH_URL, REDIRECT_URL, SPOTIFY_LOGIN_REDIRECT,
				MusicService.SPOTIFY, SPOTIFY_CODE),
			Arguments.of(YT_MUSIC_AUTH_URL, REDIRECT_URL, YT_MUSIC_LOGIN_REDIRECT,
				MusicService.YT_MUSIC, YT_MUSIC_CODE)
		);
	}

	@ParameterizedTest
	@ValueSource(strings = { SPOTIFY_AUTH_URL, YT_MUSIC_AUTH_URL })
	void testReturn400IfNoRedirectUrlProvided(String authUrl) throws Exception {
		mvc.perform(get(authUrl))
			.andDo(print())
			.andExpect(status().is(400));
	}

	@ParameterizedTest
	@ValueSource(strings = { SPOTIFY_AUTH_URL, YT_MUSIC_AUTH_URL })
	void testReturn401IfInvalidJwtProvided(String authUrl) throws Exception {
		mvc.perform(get(authUrl)
				.param("redirectUrl", REDIRECT_URL)
				.param("jwt", "335336"))
			.andDo(print())
			.andExpect(status().is(401));
	}

	@ParameterizedTest
	@MethodSource("provideArgumentsForCallbackTest")
	void testRedirectToClientWithAccessTokenOnCallbackCall(
		String authUrl,
		String redirectUrl,
		String callbackUrl,
		String authCode
	) throws Exception {
		var callback = performAuthRedirect(authUrl, redirectUrl);
		var state = callback.substring(callback.indexOf("&state=") + 7);

		var redirect = mvc.perform(get(callbackUrl)
				.param("code", authCode)
				.param("state", state))
			.andDo(print())
			.andExpect(status().is3xxRedirection())
			.andReturn()
			.getResponse()
			.getRedirectedUrl();

		var accessToken = redirect.substring(redirect.indexOf("?access_token=") + 14);
		assertNotNull(accessToken);
	}

	private static Stream<Arguments> provideArgumentsForCallbackTest() {
		return Stream.of(
			Arguments.of(SPOTIFY_AUTH_URL, REDIRECT_URL, SPOTIFY_CALLBACK_URL, SPOTIFY_CODE),
			Arguments.of(YT_MUSIC_AUTH_URL, REDIRECT_URL, YT_MUSIC_CALLBACK_URL, YT_MUSIC_CODE)
		);
	}

	@ParameterizedTest
	@MethodSource("provideArgumentsForCallbackTest")
	void testReturn403IfErrorProvided(
		String authUrl,
		String redirectUrl,
		String callbackUrl
	) throws Exception {
		var callback = performAuthRedirect(authUrl, redirectUrl);
		var state = encodeStateService.decode(
			callback.substring(callback.indexOf("&state=") + 7)
		);

		mvc.perform(get(callbackUrl)
				.param("error", "ERROR")
				.param("state", encodeStateService.encode(state)))
			.andDo(print())
			.andExpect(status().is(403));
	}

	@ParameterizedTest
	@MethodSource("provideArgumentsForCallbackTest")
	void testReturn400IfInvalidUuidProvided(
		String authUrl,
		String redirectUrl,
		String callbackUrl,
		String authCode
	) throws Exception {
		var callback = performAuthRedirect(authUrl, redirectUrl);
		var state = encodeStateService.decode(
			callback.substring(callback.indexOf("&state=") + 7)
		);
		state.setUuid(
			encryptionService.encrypt("mkhjklmknj", restProperties.uuidSecret())
		);

		mvc.perform(get(callbackUrl)
				.param("code", authCode)
				.param("state", encodeStateService.encode(state)))
			.andDo(print())
			.andExpect(status().is(401));
	}

	@ParameterizedTest
	@MethodSource("provideArgumentsForCallbackTest")
	void testReturn400IfInvalidJwtProvided(
		String authUrl,
		String redirectUrl,
		String callbackUrl,
		String authCode
	) throws Exception {
		var callback = performAuthRedirect(authUrl, redirectUrl);
		var state = encodeStateService.decode(
			callback.substring(callback.indexOf("&state=") + 7)
		);
		var uuid = encryptionService.decrypt(state.getUuid(),
			restProperties.uuidSecret());
		state.setJwt(
			encryptionService.encrypt("mkhjklmklfkemlnj", uuid)
		);

		mvc.perform(get(callbackUrl)
				.param("code", authCode)
				.param("state", encodeStateService.encode(state)))
			.andDo(print())
			.andExpect(status().is(401));
	}
}
