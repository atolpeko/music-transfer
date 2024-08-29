package com.mf.queue;

import com.mf.queue.service.RequestQueue;
import com.mf.queue.service.impl.TimeWindowRateLimiter;
import com.mf.queue.service.impl.DaemonRequestQueueWorker;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.client.RestTemplate;

public class RequestQueueWorkerFactory {

	public static class DaemonRequestQueueWorkerBuilder {

		private RequestQueue requestQueue;
		private RestTemplate restTemplate;
		private RedisTemplate<String, String> redisTemplate;
		private int requestsPerWindow;
		private int windowSeconds;
		private int waitSeconds;

		public DaemonRequestQueueWorkerBuilder withRequestQueue(RequestQueue queue) {
			this.requestQueue = queue;
			return this;
		}

		public DaemonRequestQueueWorkerBuilder withRestTemplate(RestTemplate restTemplate) {
			this.restTemplate = restTemplate;
			return this;
		}

		public DaemonRequestQueueWorkerBuilder withRedisTemplate(
			RedisTemplate<String, String> redisTemplate
		) {
			this.redisTemplate = redisTemplate;
			return this;
		}

		public DaemonRequestQueueWorkerBuilder windowSeconds(int seconds) {
			this.windowSeconds = seconds;
			return this;
		}

		public DaemonRequestQueueWorkerBuilder requestsPerWindow(int requests) {
			this.requestsPerWindow = requests;
			return this;
		}

		public DaemonRequestQueueWorkerBuilder waitSeconds(int seconds) {
			this.waitSeconds = seconds;
			return this;
		}

		public DaemonRequestQueueWorker build() {
			var limiter = new TimeWindowRateLimiter(redisTemplate, requestsPerWindow, windowSeconds);
			return new DaemonRequestQueueWorker(
				requestQueue,
				limiter,
				restTemplate,
				waitSeconds
			);
		}
	}

	private RequestQueueWorkerFactory() {}

	public static DaemonRequestQueueWorkerBuilder daemonRequestQueueWorkerBuilder() {
		return new DaemonRequestQueueWorkerBuilder();
	}
}
