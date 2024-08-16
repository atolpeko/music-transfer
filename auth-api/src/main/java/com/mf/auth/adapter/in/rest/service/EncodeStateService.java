package com.mf.auth.adapter.in.rest.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.mf.auth.adapter.in.rest.valueobject.AuthState;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EncodeStateService {

	private final ObjectMapper mapper;

	public String encode(AuthState state) throws JsonProcessingException {
		var jsonBytes = mapper.writeValueAsString(state).getBytes(StandardCharsets.UTF_8);
		var base64Encoded = Base64.getEncoder().encodeToString(jsonBytes);
		return URLEncoder.encode(base64Encoded, StandardCharsets.UTF_8);
	}

	public AuthState decode(String state) throws Exception {
		var urlDecoded = URLDecoder.decode(state, StandardCharsets.UTF_8);
		var bytes = Base64.getDecoder().decode(urlDecoded);
		var json = new String(bytes, StandardCharsets.UTF_8);
		return mapper.readValue(json, AuthState.class);
	}
}
