package com.mf.queue.service.impl;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class RequestQueueThreadFactory implements ThreadFactory {

	private final AtomicInteger counter = new AtomicInteger(0);

	@Override
	public Thread newThread(Runnable runnable) {
		var i = counter.incrementAndGet();
		var thread = new Thread(runnable);
		thread.setName("worker:thread-" + i);
		return thread;
	}
}
