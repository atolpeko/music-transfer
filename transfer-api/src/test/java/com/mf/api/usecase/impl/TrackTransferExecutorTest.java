package com.mf.api.usecase.impl;

import static com.mf.api.fixture.TrackTransferExecutorFixture.SOME_TRACKS;
import static com.mf.api.fixture.TrackTransferExecutorFixture.TOKEN;
import static com.mf.api.fixture.TrackTransferExecutorFixture.TRACKS;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mf.api.config.UnitTest;
import com.mf.api.domain.entity.Track;
import com.mf.api.port.MusicServicePort;
import com.mf.api.usecase.valueobject.TransferContext;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@UnitTest
class TrackTransferExecutorTest {

	@InjectMocks
	TrackTransferExecutor target;

	@Mock
	TrackSearcher trackSearcher;

	@Mock
	MusicServicePort service;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	void testTransfersSelectedTracks() {
		when(trackSearcher.search(service, TOKEN, TRACKS)).thenReturn(TRACKS);

		var result = target.transfer(getContext());
		verify(service, times(1)).trackBulkLike(TOKEN, TRACKS);
		assertEquals(TRACKS.size(), result.getTransferredCount());
		assertTrue(result.getFailed().isEmpty());
	}

	private TransferContext<List<Track>> getContext() {
		return TransferContext.<List<Track>>builder()
			.source("source")
			.target("target")
			.toTransfer(TRACKS)
			.service(service)
			.token(TOKEN)
			.build();
	}

	@Test
	void testTransfersNotAllTracks() {
		when(trackSearcher.search(service, TOKEN, TRACKS)).thenReturn(SOME_TRACKS);

		var result = target.transfer(getContext());
		verify(service, times(1)).trackBulkLike(TOKEN, SOME_TRACKS);
		assertEquals(SOME_TRACKS.size(), result.getTransferredCount());
		assertEquals(TRACKS.size() - SOME_TRACKS.size(), result.getFailed().size());
	}

	@Test
	void testTransferWithNoTracks() {
		var result = target.transfer(getContext());
		assertEquals(0, result.getTransferredCount());
		assertEquals(TRACKS.size(), result.getFailed().size());
	}
}
