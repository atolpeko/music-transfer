package com.mf.api.usecase.impl;

import static com.mf.api.fixture.TransferUseCaseImplFixture.FAILED_PLAYLIST;
import static com.mf.api.fixture.TransferUseCaseImplFixture.NEXT;
import static com.mf.api.fixture.TransferUseCaseImplFixture.PLAYLIST_PAGE_1;
import static com.mf.api.fixture.TransferUseCaseImplFixture.PLAYLIST_PAGE_2;
import static com.mf.api.fixture.TransferUseCaseImplFixture.TRACK_PAGE_1;
import static com.mf.api.fixture.TransferUseCaseImplFixture.TRACK_PAGE_2;
import static com.mf.api.fixture.TransferUseCaseImplFixture.SOME_TRACKS;
import static com.mf.api.fixture.TransferUseCaseImplFixture.PLAYLIST_1;
import static com.mf.api.fixture.TransferUseCaseImplFixture.PLAYLIST_TRANSFER_REQUEST;
import static com.mf.api.fixture.TransferUseCaseImplFixture.SOURCE;
import static com.mf.api.fixture.TransferUseCaseImplFixture.SRC_TOKEN;
import static com.mf.api.fixture.TransferUseCaseImplFixture.TARGET;
import static com.mf.api.fixture.TransferUseCaseImplFixture.TARGET_TOKEN;
import static com.mf.api.fixture.TransferUseCaseImplFixture.TOKEN_MAP;
import static com.mf.api.fixture.TransferUseCaseImplFixture.TRACKS;
import static com.mf.api.fixture.TransferUseCaseImplFixture.TRACK_TRANSFER_REQUEST;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.Mockito.when;

import com.mf.api.config.UnitTest;
import com.mf.api.domain.entity.Playlist;
import com.mf.api.domain.entity.Track;
import com.mf.api.port.MusicServicePort;
import com.mf.api.port.exception.AccessException;
import com.mf.api.port.exception.MusicServiceException;
import com.mf.api.usecase.entity.TransferResult;
import com.mf.api.usecase.exception.AuthorizationException;
import com.mf.api.usecase.exception.InvalidRequestException;
import com.mf.api.usecase.exception.UseCaseException;
import com.mf.api.usecase.valueobject.ServiceMap;
import com.mf.api.usecase.valueobject.TransferContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
	MusicServicePort sourceSvc;

	@Mock
	MusicServicePort targetSvc;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.initMocks(this);
		when(serviceMap.get(SOURCE)).thenReturn(sourceSvc);
		when(serviceMap.get(TARGET)).thenReturn(targetSvc);
	}

	@Test
	void testReturnsAvailableTracksWithPagination() {
		when(sourceSvc.likedTracks(SRC_TOKEN, null)).thenReturn(TRACK_PAGE_1);
		when(sourceSvc.likedTracks(SRC_TOKEN, NEXT)).thenReturn(TRACK_PAGE_2);

		var tracks = new ArrayList<Track>(4);
		String next = null;
		do {
			var result = target.findTracks(SOURCE, TOKEN_MAP, next);
			tracks.addAll(result.getItems());
			next = result.getNext();
		} while (next != null);

		assertEquals(TRACKS.size(), tracks.size());
	}

	@Test
	void testReturnsAvailablePlaylistsWithPagination() {
		when(sourceSvc.playlists(SRC_TOKEN, null)).thenReturn(PLAYLIST_PAGE_1);
		when(sourceSvc.playlists(SRC_TOKEN, NEXT)).thenReturn(PLAYLIST_PAGE_2);

		var playlists = new ArrayList<Playlist>(2);
		String next = null;
		do {
			var result = target.findPlaylists(SOURCE, TOKEN_MAP, next);
			playlists.addAll(result.getItems());
			next = result.getNext();
		} while (next != null);

		assertEquals(2, playlists.size());
	}

	@Test
	void testTransfersSelectedTracks() {
		when(trackTransferExecutor.transfer(getTrackContext()))
			.thenReturn(TransferResult.of(10, Collections.emptyList()));

		var result = target.transferTracks(TRACK_TRANSFER_REQUEST);
		assertEquals(10, result.getTransferredCount());
		assertEquals(0, result.getFailed().size());
	}

	private TransferContext<List<Track>> getTrackContext() {
		return TransferContext.<List<Track>>builder()
			.source(TRACK_TRANSFER_REQUEST.getSource())
			.target(TRACK_TRANSFER_REQUEST.getTarget())
			.toTransfer(TRACKS)
			.service(targetSvc)
			.token(TARGET_TOKEN)
			.build();
	}

	@Test
	void testTransfersSomeTracks() {
		when(trackTransferExecutor.transfer(getTrackContext()))
			.thenReturn(TransferResult.of(10, SOME_TRACKS));

		var result = target.transferTracks(TRACK_TRANSFER_REQUEST);
		assertEquals(10, result.getTransferredCount());
		assertEquals(SOME_TRACKS.size(), result.getFailed().size());
	}

	@Test
	void testTransfersSelectedPlaylist() {
		when(playlistTransferExecutor.transfer(getPlaylistContext()))
			.thenReturn(TransferResult.of(1, null));

		var result = target.transferPlaylist(PLAYLIST_TRANSFER_REQUEST);
		assertEquals(1, result.getTransferredCount());
		assertNull(result.getFailed());
	}

	private TransferContext<Playlist> getPlaylistContext() {
		return TransferContext.<Playlist>builder()
			.source(PLAYLIST_TRANSFER_REQUEST.getSource())
			.target(PLAYLIST_TRANSFER_REQUEST.getTarget())
			.toTransfer(PLAYLIST_1)
			.service(targetSvc)
			.token(TARGET_TOKEN)
			.build();
	}

	@Test
	void testTransferSomeTracksInPlaylists() {
		when(playlistTransferExecutor.transfer(getPlaylistContext()))
			.thenReturn(TransferResult.of(1, FAILED_PLAYLIST));

		var result = target.transferPlaylist(PLAYLIST_TRANSFER_REQUEST);
		assertEquals(1, result.getTransferredCount());
		assertEquals(TRACKS.size() - SOME_TRACKS.size(), result.getFailed().getTracks().size());
	}

	@Test
	void testThrowsExceptionWhenTargetIsInvalid() {
		when(serviceMap.get(TARGET)).thenReturn(null);
		assertThrows(
			InvalidRequestException.class,
			() -> target.transferTracks(TRACK_TRANSFER_REQUEST)
		);
	}

	@Test
	void testThrowsExceptionWhenTrackTransferAuthorizationFails() {
		when(trackTransferExecutor.transfer(getTrackContext()))
			.thenThrow(AccessException.class);
		assertThrows(
			AuthorizationException.class,
			() -> target.transferTracks(TRACK_TRANSFER_REQUEST)
		);
	}

	@Test
	void testThrowsExceptionWhenPlaylistTransferAuthorizationFails() {
		when(playlistTransferExecutor.transfer(getPlaylistContext()))
			.thenThrow(AccessException.class);
		assertThrows(
			AuthorizationException.class,
			() -> target.transferPlaylist(PLAYLIST_TRANSFER_REQUEST)
		);
	}

	@Test
	void testThrowsExceptionWhenTrackTransferOperationIsNotSupported() {
		when(trackTransferExecutor.transfer(getTrackContext()))
			.thenThrow(UnsupportedOperationException.class);
		assertThrows(
			InvalidRequestException.class,
			() -> target.transferTracks(TRACK_TRANSFER_REQUEST)
		);
	}

	@Test
	void testThrowsExceptionWhenPlaylistTransferOperationIsNotSupported() {
		when(playlistTransferExecutor.transfer(getPlaylistContext()))
			.thenThrow(UnsupportedOperationException.class);
		assertThrows(
			InvalidRequestException.class,
			() -> target.transferPlaylist(PLAYLIST_TRANSFER_REQUEST)
		);
	}

	@Test
	void testThrowsExceptionWhenTrackTransferFails() {
		when(trackTransferExecutor.transfer(getTrackContext()))
			.thenThrow(MusicServiceException.class);
		assertThrows(
			UseCaseException.class,
			() -> target.transferTracks(TRACK_TRANSFER_REQUEST)
		);
	}

	@Test
	void testThrowsExceptionWhenPlaylistTransferFails() {
		when(playlistTransferExecutor.transfer(getPlaylistContext()))
			.thenThrow(MusicServiceException.class);
		assertThrows(
			UseCaseException.class,
			() -> target.transferPlaylist(PLAYLIST_TRANSFER_REQUEST)
		);
	}
}
