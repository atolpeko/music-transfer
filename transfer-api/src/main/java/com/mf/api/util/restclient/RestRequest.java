package com.mf.api.util.restclient;

import com.mf.api.domain.entity.OAuth2Token;

import java.util.ArrayList;
import java.util.List;

import lombok.Builder;
import lombok.Data;

import org.springframework.http.HttpMethod;

@Data
@Builder
public class RestRequest {

	private String url;
	private HttpMethod method;
	private OAuth2Token token;
	private String json;
	private Integer offset;
	private Integer limit;

	@Builder.Default
	private boolean retryIfFails = false;

	@Builder.Default
	private List<Param> params = new ArrayList <>();
}
