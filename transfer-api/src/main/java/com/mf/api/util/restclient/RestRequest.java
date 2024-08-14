package com.mf.api.util.restclient;

import com.mf.api.domain.entity.OAuth2Token;
import com.mf.api.util.type.Tuple;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import org.springframework.http.HttpMethod;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class RestRequest {

	@Accessors
	private String url;

	@Accessors
	private HttpMethod method;

	@Accessors
	private OAuth2Token token;

	@Accessors
	private String json;

	@Accessors
	@Builder.Default
	private boolean retryIfFails = false;

	@Accessors
	@Builder.Default
	private List<Param> params = new ArrayList<>();

	private Tuple<String, Integer> offset;
	private Tuple<String, Integer> limit;

	public Integer getOffset() {
		if (offset == null) {
			return null;
		}

		return offset.getSecond();
	}

	public Integer getLimit() {
		if (limit == null) {
			return null;
		}

		return limit.getSecond();
	}

	public String getOffsetName() {
		if (offset == null) {
			return null;
		}

		return offset.getFirst();
	}

	public String getLimitName() {
		if (limit == null) {
			return null;
		}

		return limit.getFirst();
	}

	public void setLimit(int limit) {
		this.limit = Tuple.of(this.limit.getFirst(), limit);
	}

	public static class RestRequestBuilder {

		public RestRequestBuilder offset(int offset) {
			this.offset = Tuple.of("offset", offset);
			return this;
		}

		public RestRequestBuilder offset(String name, int offset) {
			this.offset = Tuple.of(name, offset);
			return this;
		}

		public RestRequestBuilder limit(int limit) {
			this.limit = Tuple.of("limit", limit);
			return this;
		}

		public RestRequestBuilder limit(String name, int limit) {
			this.limit = Tuple.of(name, limit);
			return this;
		}
	}
}
