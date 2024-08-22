package com.mf.api.usecase.impl;

import static com.mf.api.fixture.TransferUseCaseImplFixture.FAILED_PLAYLIST_TRACKS;
import static com.mf.api.fixture.TransferUseCaseImplFixture.FAILED_TRACKS;
import static com.mf.api.fixture.TransferUseCaseImplFixture.FULL_TRANSFER_REQUEST;
import static com.mf.api.fixture.TransferUseCaseImplFixture.REQUEST_INVALID_SOURCE_TARGET;
import static com.mf.api.fixture.TransferUseCaseImplFixture.SOURCE;
import static com.mf.api.fixture.TransferUseCaseImplFixture.SRC_TOKEN;
import static com.mf.api.fixture.TransferUseCaseImplFixture.TARGET;
import static com.mf.api.fixture.TransferUseCaseImplFixture.TARGET_TOKEN;
import static com.mf.api.fixture.TransferUseCaseImplFixture.TRACK_TRANSFER_REQUEST;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.Mockito.when;

import com.mf.api.config.UnitTest;
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
class TransferUseCaseImplTest {

	@InjectMocks
	TransferUseCaseImpl target;

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
