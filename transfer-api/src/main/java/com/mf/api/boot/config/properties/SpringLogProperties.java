package com.mf.api.boot.config.properties;

import lombok.Getter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class SpringLogProperties {

	@Value("${logging.level.requestQueue}")
	private String requestQueueLevel;
}
