package com.mf.auth.boot.config;

import com.mf.auth.boot.config.properties.SpringLogProperties;
import com.mf.auth.boot.config.properties.SpringRequestQueueProperties;

import com.mf.queue.RequestQueueWorkerFactory;
import com.mf.queue.config.RequestQueueConfigurator;
import com.mf.queue.service.RequestQueue;
import com.mf.queue.service.RequestQueueWorker;

import org.apache.logging.log4j.Level;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RequestQueueConfig {

	@Bean
	public RequestQueue requestQueue() {
		return new RequestQueue();
	}

	@Bean(initMethod = "start", destroyMethod = "kill")
	public RequestQueueWorker requestQueueWorker(
		RequestQueue queue,
		RestTemplate restTemplate,
		RedisTemplate<String, String> redisTemplate,
		SpringRequestQueueProperties properties,
		SpringLogProperties logProperties
	) {
		var level = Level.getLevel(logProperties.getRequestQueueLevel());
		RequestQueueConfigurator.setLogLevel(level);

		return RequestQueueWorkerFactory.daemonRequestQueueWorkerBuilder()
			.withRequestQueue(queue)
			.withRestTemplate(restTemplate)
			.withRedisTemplate(redisTemplate)
			.requestsPerWindow(properties.getRequestsPerWindow())
			.windowSeconds(properties.getRequestWindowSeconds())
			.waitSeconds(properties.getWaitSeconds())
			.build();
	}
}
