package com.mf.api.adapter.in.rest.controller;

import com.mf.api.adapter.in.rest.api.TransferAPI;
import com.mf.api.adapter.in.rest.entity.TransferResult;
import com.mf.api.adapter.in.rest.mapper.TrackMapper;
import com.mf.api.adapter.in.rest.entity.MusicService;
import com.mf.api.usecase.UseCase;
import com.mf.api.usecase.entity.TransferRequest;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TransferController implements TransferAPI {

	private final UseCase useCase;
	private final TrackMapper trackMapper;

	@Override
	public TransferResult transfer(
		MusicService source,
		MusicService target,
		Boolean transferPlaylists,
		String authToken
	) {
		var jwt = authToken.replace("Bearer ", "");
		var request = TransferRequest.builder()
			.source(source.name())
			.target(target.name())
			.transferPlaylists(transferPlaylists)
			.jwt(jwt)
			.build();

		var result = useCase.transfer(request);
		var failed = result.getFailedToTransfer().stream()
			.map(trackMapper::toRestEntity)
			.toList();

		return TransferResult.builder()
			.transferredTracks(result.getTransferredTracks())
			.transferredPlaylists(result.getTransferredPlaylists())
			.failedToTransfer(failed)
			.build();
	}
}
