package com.mf.api.adapter.out.musicservice;

import com.mf.api.port.MusicServicePort;
import com.mf.api.util.restclient.PageableRestResponse;
import com.mf.api.util.restclient.RestClient;
import com.mf.api.util.restclient.RestRequest;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class BaseMusicServiceAdapter implements MusicServicePort {

	protected final RestClient restClient;

	protected <T> List<T> fetchAllPages(
		RestRequest request,
		Function<LinkedHashMap<Object, Object>, List<T>> mapper
	) {
		var items = new LinkedList<T>();
		var nextOffset = 0;
		PageableRestResponse<T> response;
		do {
			var req = RestRequest.builder()
				.url(request.getUrl())
				.method(request.getMethod())
				.params(request.getParams())
				.offset(request.getOffsetName(), nextOffset)
				.limit(request.getLimitName(), request.getLimit())
				.token(request.getToken())
				.retryIfFails(request.isRetryIfFails())
				.build();

			response = restClient.requestPage(req, mapper);
			items.addAll(response.getItems());
			nextOffset = response.getPrevOffset() + request.getLimit();
		} while (response.hasNext());

		return items;
	}
}
