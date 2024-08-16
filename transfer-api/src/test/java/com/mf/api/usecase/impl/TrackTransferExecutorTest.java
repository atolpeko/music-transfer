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
import com.mf.api.port.MusicServicePort;
import com.mf.api.usecase.valueobject.TransferContext;

import java.util.Collections;

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
	MusicServicePort sourceSvc;

	@Mock
	MusicServicePort targetSvc;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	void testTransfersAllTracks() {
		when(sourceSvc.likedTracks(TOKEN)).thenReturn(TRACKS);
		when(trackSearcher.search(targetSvc, TOKEN, TRACKS)).thenReturn(TRACKS);

		var result = target.transfer(getContext());
		verify(targetSvc, times(1)).trackBulkLike(TOKEN, TRACKS);
		assertEquals(TRACKS.size(), result.getFirst());
		assertTrue(result.getSecond().isEmpty());
	}

	private TransferContext getContext() {
		return TransferContext.builder()
			.source("source")
			.target("target")
			.sourceSvc(sourceSvc)
			.targetSvc(targetSvc)
			.sourceToken(TOKEN)
			.targetToken(TOKEN)
			.build();
	}

	@Test
	void testTransfersNotAllTracks() {
		when(sourceSvc.likedTracks(TOKEN)).thenReturn(TRACKS);
		when(trackSearcher.search(targetSvc, TOKEN, TRACKS)).thenReturn(SOME_TRACKS);

		var result = target.transfer(getContext());
		verify(targetSvc, times(1)).trackBulkLike(TOKEN, SOME_TRACKS);
		assertEquals(SOME_TRACKS.size(), result.getFirst());
		assertEquals(TRACKS.size() - SOME_TRACKS.size(), result.getSecond().size());
	}

	@Test
	void testTransferWithNoTracks() {
		when(sourceSvc.likedTracks(TOKEN)).thenReturn(Collections.emptyList());

		var result = target.transfer(getContext());
		assertEquals(0, result.getFirst());
		assertTrue(result.getSecond().isEmpty());
	}
}
