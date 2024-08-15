package com.mf.api.usecase.impl;

import static com.mf.api.fixture.TrackSearcherFixture.BY_NAME_1;
import static com.mf.api.fixture.TrackSearcherFixture.BY_NAME_2;
import static com.mf.api.fixture.TrackSearcherFixture.BY_NAME_AND_ARTIST_1;
import static com.mf.api.fixture.TrackSearcherFixture.BY_NAME_AND_ARTIST_2;
import static com.mf.api.fixture.TrackSearcherFixture.FULL_CRITERIA_1;
import static com.mf.api.fixture.TrackSearcherFixture.FULL_CRITERIA_2;
import static com.mf.api.fixture.TrackSearcherFixture.TOKEN;
import static com.mf.api.fixture.TrackSearcherFixture.TRACKS;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.mf.api.config.UnitTest;
import com.mf.api.port.MusicServicePort;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;

@UnitTest
class TrackSearcherTest {

	TrackSearcher target;

	MusicServicePort service;

	@BeforeEach
	void setUp() {
		target = new TrackSearcher();
		service = Mockito.mock(MusicServicePort.class);
	}

	@Test
	void testFindsTracksByFullCriteria() {
		when(service.searchTracks(TOKEN, FULL_CRITERIA_1))
			.thenReturn(Optional.of(TRACKS.get(0)));
		when(service.searchTracks(TOKEN, FULL_CRITERIA_2))
			.thenReturn(Optional.of(TRACKS.get(1)));

		var found = target.search(service, TOKEN, TRACKS);
		assertEquals(TRACKS, found);
	}

	@Test
	void testFindsTracksByNameAndArtist() {
		when(service.searchTracks(TOKEN, FULL_CRITERIA_1))
			.thenReturn(Optional.empty());
		when(service.searchTracks(TOKEN, FULL_CRITERIA_2))
			.thenReturn(Optional.empty());
		when(service.searchTracks(TOKEN, BY_NAME_AND_ARTIST_1))
			.thenReturn(Optional.of(TRACKS.get(0)));
		when(service.searchTracks(TOKEN, BY_NAME_AND_ARTIST_2))
			.thenReturn(Optional.of(TRACKS.get(1)));

		var found = target.search(service, TOKEN, TRACKS);
		assertEquals(TRACKS, found);
	}

	@Test
	void testFindsTracksByName() {
		when(service.searchTracks(TOKEN, FULL_CRITERIA_1))
			.thenReturn(Optional.empty());
		when(service.searchTracks(TOKEN, FULL_CRITERIA_2))
			.thenReturn(Optional.empty());
		when(service.searchTracks(TOKEN, BY_NAME_AND_ARTIST_1))
			.thenReturn(Optional.empty());
		when(service.searchTracks(TOKEN, BY_NAME_AND_ARTIST_2))
			.thenReturn(Optional.empty());
		when(service.searchTracks(TOKEN, BY_NAME_1))
			.thenReturn(Optional.of(TRACKS.get(0)));
		when(service.searchTracks(TOKEN, BY_NAME_2))
			.thenReturn(Optional.of(TRACKS.get(1)));

		var found = target.search(service, TOKEN, TRACKS);
		assertEquals(TRACKS, found);
	}
}
