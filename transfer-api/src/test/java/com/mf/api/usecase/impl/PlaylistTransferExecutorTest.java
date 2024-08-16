package com.mf.api.usecase.impl;

import static com.mf.api.fixture.PlaylistTransferExecutorFixture.PLAYLISTS;
import static com.mf.api.fixture.PlaylistTransferExecutorFixture.SOME_TRACKS_1;
import static com.mf.api.fixture.PlaylistTransferExecutorFixture.SOME_TRACKS_2;
import static com.mf.api.fixture.PlaylistTransferExecutorFixture.TOKEN;

import static com.mf.api.fixture.PlaylistTransferExecutorFixture.TRACKS_1;
import static com.mf.api.fixture.PlaylistTransferExecutorFixture.TRACKS_2;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.ArgumentMatchers.any;
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
class PlaylistTransferExecutorTest {

	@InjectMocks
	PlaylistTransferExecutor target;

	@Mock
	TrackSearcher trackSearcher;

	@Mock
	MusicServicePort sourceSvc;

	@Mock
	MusicServicePort targetSvc;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.initMocks(this);
		when(sourceSvc.playlists(TOKEN)).thenReturn(PLAYLISTS);
		when(trackSearcher.search(targetSvc, TOKEN, TRACKS_1)).thenReturn(TRACKS_1);
		when(trackSearcher.search(targetSvc, TOKEN, TRACKS_2)).thenReturn(TRACKS_2);

		PLAYLISTS.forEach(playlist -> when(targetSvc.createPlaylist(TOKEN, playlist))
			.thenReturn(playlist.getId()));
	}

	@Test
	void testTransfersAllPlaylists() {
		var result = target.transfer(getContext());

		verify(targetSvc, times(PLAYLISTS.size())).createPlaylist(any(), any());
		PLAYLISTS.forEach(playlist -> verify(targetSvc, times(1))
			.addToPlaylist(TOKEN, playlist.getId(), playlist.getTracks()));
		assertEquals(PLAYLISTS.size(), result.getFirst());
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
		when(trackSearcher.search(targetSvc, TOKEN, TRACKS_1))
			.thenReturn(SOME_TRACKS_1);
		when(trackSearcher.search(targetSvc, TOKEN, TRACKS_2))
			.thenReturn(SOME_TRACKS_2);

		var result = target.transfer(getContext());

		verify(targetSvc, times(PLAYLISTS.size())).createPlaylist(any(), any());
		assertEquals(PLAYLISTS.size(), result.getFirst());
		assertEquals(
			TRACKS_1.size() + TRACKS_2.size() - SOME_TRACKS_1.size() - SOME_TRACKS_2.size(),
			result.getSecond().size()
		);
	}

	@Test
	void testTransferWithNoTracks() {
		when(sourceSvc.playlists(TOKEN)).thenReturn(Collections.emptyList());

		var result = target.transfer(getContext());
		assertEquals(0, result.getFirst());
		assertTrue(result.getSecond().isEmpty());
	}
}
