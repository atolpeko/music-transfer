package com.mf.api.usecase.entity;

import com.mf.api.domain.entity.Track;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferResult {

	private int transferredTracks;
	private int transferredPlaylists;
	private List<Track> failedToTransfer;
}
