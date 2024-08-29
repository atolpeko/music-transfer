package com.mf.api.boot.config.properties;

import lombok.Getter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class SpringRequestQueueProperties {

	@Value("${service.requestQueue.requestsPerWindow}")
	private int requestsPerWindow;

	@Value("${service.requestQueue.requestWindowSeconds}")
	private int requestWindowSeconds;

	@Value("${service.requestQueue.waitSeconds}")
	private int waitSeconds;
}
