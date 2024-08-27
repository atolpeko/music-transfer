package com.mf.api.usecase.impl;

import static com.mf.api.fixture.PlaylistTransferExecutorFixture.PLAYLIST;
import static com.mf.api.fixture.PlaylistTransferExecutorFixture.SOME_TRACKS;
import static com.mf.api.fixture.PlaylistTransferExecutorFixture.TOKEN;
import static com.mf.api.fixture.PlaylistTransferExecutorFixture.TRACKS;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mf.api.config.UnitTest;
import com.mf.api.domain.entity.Playlist;
import com.mf.api.port.MusicServicePort;
import com.mf.api.usecase.valueobject.TransferContext;

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
	MusicServicePort service;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.initMocks(this);
		when(service.createPlaylist(TOKEN, PLAYLIST)).thenReturn(PLAYLIST.getServiceId());
	}

	@Test
	void testTransfersSelectedPlaylist() {
		when(trackSearcher.search(service, TOKEN, TRACKS)).thenReturn(TRACKS);

		var result = target.transfer(getContext());

		verify(service, times(1)).createPlaylist(TOKEN, PLAYLIST);
		verify(service, times(1)).addToPlaylist(TOKEN, PLAYLIST.getServiceId(), PLAYLIST.getTracks());
		assertEquals(1, result.getTransferredCount());
		assertNull(result.getFailed());
	}

	private TransferContext<Playlist> getContext() {
		return TransferContext.<Playlist>builder()
			.source("source")
			.target("target")
			.toTransfer(PLAYLIST)
			.service(service)
			.token(TOKEN)
			.build();
	}

	@Test
	void testTransfersSelectedPlaylistWithSomeTracks() {
		when(trackSearcher.search(service, TOKEN, TRACKS)).thenReturn(SOME_TRACKS);

		var result = target.transfer(getContext());

		verify(service, times(1)).createPlaylist(TOKEN, PLAYLIST);
		verify(service, times(1)).addToPlaylist(TOKEN, PLAYLIST.getServiceId(), SOME_TRACKS);
		assertEquals(1, result.getTransferredCount());
		assertEquals(SOME_TRACKS.size(), result.getFailed().getTracks().size());
		assertEquals(TRACKS.size() - SOME_TRACKS.size(), result.getFailed().getTracks().size());
	}
}
