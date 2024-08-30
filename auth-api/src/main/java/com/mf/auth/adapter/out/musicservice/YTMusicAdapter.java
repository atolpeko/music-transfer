package com.mf.auth.adapter.out.musicservice;

import com.mf.auth.adapter.properties.MusicServiceProperties;
import com.mf.queue.service.RequestQueue;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class YTMusicAdapter extends StandardOauth2MusicService {

	public YTMusicAdapter(MusicServiceProperties properties, RequestQueue requestQueue) {
		super(properties, requestQueue);
	}

	@Override
	public String toString() {
		return properties.name();
	}
}
