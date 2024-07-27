package com.mf.auth.adapter.out.musicservice;

import com.mf.auth.adapter.properties.MusicServiceProperties;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.client.RestTemplate;

@Log4j2
public class SpotifyAdapter extends BaseMusicService {

	public SpotifyAdapter(MusicServiceProperties properties, RestTemplate restTemplate) {
		super(properties, restTemplate);
	}

	@Override
	public String toString() {
		return "SPOTIFY";
	}
}
