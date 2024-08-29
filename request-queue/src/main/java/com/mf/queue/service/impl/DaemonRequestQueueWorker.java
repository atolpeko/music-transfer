package com.mf.queue.service.impl;

import com.mf.queue.entity.Request;
import com.mf.queue.exception.InvalidUrlException;
import com.mf.queue.service.RateLimiter;
import com.mf.queue.service.RequestQueue;
import com.mf.queue.service.RequestQueueWorker;

import java.util.HashMap;
import java.util.Map;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.web.client.RestTemplate;

@Log4j2
@Builder
@RequiredArgsConstructor
public class DaemonRequestQueueWorker implements RequestQueueWorker {

    private final Map<String, RequestQueue> serviceQueues = new HashMap<>();
    private final Map<String, Thread> serviceThreads = new HashMap<>();

    private final RequestQueue requestQueue;
    private final RateLimiter rateLimiter;
    private final RestTemplate restTemplate;
    private final int waitSeconds;

    @Override
    public void start() {
        Runnable task = () -> {
            log.info("Starting request queue");
            while (!Thread.currentThread().isInterrupted()) {
                Request<?, ?> request = null;
                try {
                    request = requestQueue.take();
                    var service = request.getHost();
                    var queue = serviceQueues.computeIfAbsent(service, key -> new RequestQueue());

                    if (!serviceThreads.containsKey(service)) {
                        var thread = new Thread(() -> runProcessing(service, queue));
                        thread.start();
                        serviceThreads.put(service, thread);
                    }

                    queue.submit(request);
                } catch (InterruptedException e) {
                    log.info("Stopping request queue");
                    Thread.currentThread().interrupt();
                    break;
                } catch (InvalidUrlException e) {
                    var msg = "Invalid request provided: %s".formatted(e.getUrl());
                    fail(request, e, msg);
                } catch (Exception e) {
                    var msg = "Exception while processing requests: %s".formatted(e.getMessage());
                    fail(request, e, msg);
                }
            }
        };

        var thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private void fail(Request<?, ?> request, Exception exception, String msg) {
        log.error(msg);
        if (request != null) {
            request.fail(exception);
        }
    }

    private void runProcessing(String service, RequestQueue queue) {
        log.info("Starting a new request queue for service: {}", service);
        while (!Thread.currentThread().isInterrupted()) {
            Request<?, ?> request = null;
            try {
                request = queue.take();
                if (rateLimiter.allowed(request)) {
                    log.debug("Executing request to {}", request.getUrl());
                    request.execute(restTemplate);
                } else {
                    log.debug("Rate limit for requests to {} exceeded. Waiting for {} seconds",
                        service, waitSeconds);
                    Thread.sleep(waitSeconds * 1000);
                    queue.submitFirst(request);
                }
            } catch (InterruptedException e) {
                log.info("Stopping request {} queue", service);
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                var msg = "Exception while processing request for %s: %s"
                    .formatted(service, e);
                fail(request, e, msg);
            }
        }
    }

    @Override
    public void stop() {
        log.info("Stopping request queue");
        serviceThreads.values().forEach(Thread::interrupt);
        Thread.currentThread().interrupt();
    }
}