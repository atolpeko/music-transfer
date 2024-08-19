package com.mf.api.config;

import com.github.tomakehurst.wiremock.WireMockServer;

import com.mf.api.boot.config.properties.SpringJwtValidatorProperties;
import com.mf.api.boot.config.properties.SpringSpotifyProperties;
import com.mf.api.boot.config.properties.SpringYTMusicProperties;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PropertyConfig {

	@Autowired
	public WireMockServer wireMockServer;

	@Autowired
	public SpringSpotifyProperties spotifyProperties;

	@Autowired
	public SpringYTMusicProperties ytMusicProperties;

	@Autowired
	public SpringJwtValidatorProperties jwtValidatorProperties;

	@PostConstruct
	void setDomain() {
		spotifyProperties.setDomain(getDomain());
		ytMusicProperties.setDomain(getDomain());
		jwtValidatorProperties.setDomain(getDomain());
	}

	private String getDomain() {
		return "http://localhost:" + wireMockServer.port();
	}
}
