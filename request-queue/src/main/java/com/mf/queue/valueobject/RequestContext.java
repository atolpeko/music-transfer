package com.mf.queue.valueobject;

import com.mf.queue.entity.Request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RequestContext {

	private final Request<?, ?> request;
	private final int timeoutMillis;
}
