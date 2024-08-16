package com.mf.api.usecase.impl;

import static com.mf.api.fixture.UseCaseImplFixture.FAILED_PLAYLIST_TRACKS;
import static com.mf.api.fixture.UseCaseImplFixture.FAILED_TRACKS;
import static com.mf.api.fixture.UseCaseImplFixture.FULL_TRANSFER_REQUEST;
import static com.mf.api.fixture.UseCaseImplFixture.INVALID_JWT;
import static com.mf.api.fixture.UseCaseImplFixture.JWT;
import static com.mf.api.fixture.UseCaseImplFixture.REQUEST_INVALID_JWT;
import static com.mf.api.fixture.UseCaseImplFixture.REQUEST_INVALID_SOURCE_TARGET;
import static com.mf.api.fixture.UseCaseImplFixture.SERVICE_TO_TOKENS;
import static com.mf.api.fixture.UseCaseImplFixture.SOURCE;
import static com.mf.api.fixture.UseCaseImplFixture.SRC_TOKEN;
import static com.mf.api.fixture.UseCaseImplFixture.TARGET;
import static com.mf.api.fixture.UseCaseImplFixture.TARGET_TOKEN;
import static com.mf.api.fixture.UseCaseImplFixture.TRACK_TRANSFER_REQUEST;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.Mockito.when;

import com.mf.api.config.UnitTest;
import com.mf.api.domain.service.JWTService;
import com.mf.api.domain.service.exception.InvalidJWTException;
import com.mf.api.port.JWTValidatorPort;
import com.mf.api.port.MusicServicePort;
import com.mf.api.port.exception.AccessException;
import com.mf.api.port.exception.MusicServiceException;
import com.mf.api.usecase.entity.TransferRequest;
import com.mf.api.usecase.exception.AuthorizationException;
import com.mf.api.usecase.exception.InvalidRequestException;
import com.mf.api.usecase.exception.UseCaseException;
import com.mf.api.usecase.valueobject.ServiceMap;
import com.mf.api.usecase.valueobject.TransferContext;
import com.mf.api.util.type.Tuple;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@UnitTest
class UseCaseImplTest {

	@InjectMocks
	UseCaseImpl target;

	@Mock
	JWTValidatorPort jwtValidator;

	@Mock
	JWTService jwtService;

	@Mock
	TrackTransferExecutor trackTransferExecutor;

	@Mock
	PlaylistTransferExecutor playlistTransferExecutor;

	@Mock
	ServiceMap serviceMap;

	@Mock
	MusicServicePort sourseSvc;

	@Mock
	MusicServicePort targetSvc;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.initMocks(this);

		when(jwtService.extractTokens(JWT)).thenReturn(SERVICE_TO_TOKENS);

		when(jwtValidator.isValid(JWT)).thenReturn(true);
		when(jwtValidator.isValid(INVALID_JWT)).thenReturn(false);

		when(serviceMap.get(SOURCE)).thenReturn(sourseSvc);
		when(serviceMap.get(TARGET)).thenReturn(targetSvc);
	}

	@Test
	void testTransferAll() {
		when(trackTransferExecutor.transfer(getContext(FULL_TRANSFER_REQUEST)))
			.thenReturn(Tuple.of(10, Collections.emptyList()));
		when(playlistTransferExecutor.transfer(getContext(FULL_TRANSFER_REQUEST)))
			.thenReturn(Tuple.of(10, Collections.emptyList()));

		var result = target.transfer(FULL_TRANSFER_REQUEST);
		assertEquals(10, result.getTransferredTracks());
		assertEquals(10, result.getTransferredPlaylists());
		assertEquals(0, result.getFailedToTransfer().size());
	}

	private TransferContext getContext(TransferRequest request) {
		return TransferContext.builder()
			.source(request.getSource())
			.target(request.getTarget())
			.sourceSvc(sourseSvc)
			.targetSvc(targetSvc)
			.sourceToken(SRC_TOKEN)
			.targetToken(TARGET_TOKEN)
			.build();
	}

	@Test
	void testTransferTracks() {
		when(trackTransferExecutor.transfer(getContext(TRACK_TRANSFER_REQUEST)))
			.thenReturn(Tuple.of(10, Collections.emptyList()));

		var result = target.transfer(TRACK_TRANSFER_REQUEST);
		assertEquals(10, result.getTransferredTracks());
		assertEquals(0, result.getTransferredPlaylists());
		assertEquals(0, result.getFailedToTransfer().size());
	}

	@Test
	void testTransferSomeTracks() {
		when(trackTransferExecutor.transfer(getContext(TRACK_TRANSFER_REQUEST)))
			.thenReturn(Tuple.of(10, FAILED_TRACKS));

		var result = target.transfer(TRACK_TRANSFER_REQUEST);
		assertEquals(10, result.getTransferredTracks());
		assertEquals(0, result.getTransferredPlaylists());
		assertEquals(FAILED_TRACKS.size(), result.getFailedToTransfer().size());
	}

	@Test
	void testTransferSomeTracksInPlaylists() {
		when(trackTransferExecutor.transfer(getContext(FULL_TRANSFER_REQUEST)))
			.thenReturn(Tuple.of(10, Collections.emptyList()));
		when(playlistTransferExecutor.transfer(getContext(FULL_TRANSFER_REQUEST)))
			.thenReturn(Tuple.of(10, FAILED_PLAYLIST_TRACKS));

		var result = target.transfer(FULL_TRANSFER_REQUEST);
		assertEquals(FAILED_PLAYLIST_TRACKS.size(), result.getFailedToTransfer().size());
	}

	@Test
	void testThrowsExceptionWhenSourceEqualsTarget() {
		assertThrows(
			InvalidRequestException.class,
			() -> target.transfer(REQUEST_INVALID_SOURCE_TARGET)
		);
	}

	@Test
	void testThrowsExceptionWhenJWTIsInvalid() {
		assertThrows(
			AuthorizationException.class,
			() -> target.transfer(REQUEST_INVALID_JWT)
		);
	}

	@Test
	void testThrowsExceptionWhenJWTIsMalformed() {
		when(jwtService.extractTokens(JWT)).thenThrow(InvalidJWTException.class);
		assertThrows(
			AuthorizationException.class,
			() -> target.transfer(FULL_TRANSFER_REQUEST)
		);
	}

	@Test
	void testThrowsExceptionWhenAuthorizationFails() {
		when(trackTransferExecutor.transfer(getContext(FULL_TRANSFER_REQUEST)))
			.thenThrow(AccessException.class);
		assertThrows(
			AuthorizationException.class,
			() -> target.transfer(FULL_TRANSFER_REQUEST)
		);
	}

	@Test
	void testThrowsExceptionWhenOperationIsNotSupported() {
		when(trackTransferExecutor.transfer(getContext(FULL_TRANSFER_REQUEST)))
			.thenThrow(UnsupportedOperationException.class);
		assertThrows(
			InvalidRequestException.class,
			() -> target.transfer(FULL_TRANSFER_REQUEST)
		);
	}

	@Test
	void testThrowsExceptionWhenOperationIsTransferFails() {
		when(trackTransferExecutor.transfer(getContext(FULL_TRANSFER_REQUEST)))
			.thenThrow(MusicServiceException.class);
		assertThrows(
			UseCaseException.class,
			() -> target.transfer(FULL_TRANSFER_REQUEST)
		);
	}
}
