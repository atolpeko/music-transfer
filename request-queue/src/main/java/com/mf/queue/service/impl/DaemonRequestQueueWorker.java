package com.mf.queue.service.impl;

import com.mf.queue.entity.Request;
import com.mf.queue.exception.InvalidUrlException;
import com.mf.queue.exception.RequestQueueException;
import com.mf.queue.exception.RequestTimeoutException;
import com.mf.queue.service.RateLimiter;
import com.mf.queue.service.RequestQueue;
import com.mf.queue.service.RequestQueueWorker;
import com.mf.queue.valueobject.RequestContext;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@Log4j2
@RequiredArgsConstructor
public class DaemonRequestQueueWorker extends Thread implements RequestQueueWorker {

    private final RequestQueue requestQueue;
    private final RateLimiter rateLimiter;
    private final RestTemplate restTemplate;
    private final int waitSeconds;
    private final ScheduledExecutorService executorService =
        Executors.newScheduledThreadPool(8, new RequestQueueThreadFactory());

    @Override
    public void start() {
        super.setName("request-queue-worker");
        super.setDaemon(true);
        super.start();
    }

    @Override
    public void run() {
        log.info("Starting request queue");
        while (!Thread.currentThread().isInterrupted()) {
            Request<?, ?> request = null;
            try {
                var context = requestQueue.take();
                request = context.getRequest();
                if (!request.actual()) {
                    fail(new RequestTimeoutException(request, "Request timed out"));
                } else if (rateLimiter.allowed(request)) {
                    executorService.schedule(
                        () -> execute(context),
                        context.getTimeoutSeconds(),
                        TimeUnit.SECONDS
                    );
                } else {
                    log.debug("Rate limit for requests to {} exceeded. Scheduling with {} seconds delay",
                        request.getHost(), waitSeconds);
                    executorService.schedule(() -> execute(context), waitSeconds, TimeUnit.SECONDS);
                }
            } catch (InterruptedException e) {
                fail(new RequestQueueException(request, "Request queue stopped"));
                log.info("Stopping request queue");
                Thread.currentThread().interrupt();
                break;
            } catch (InvalidUrlException e) {
                fail(e);
            } catch (Exception e) {
                var msg = "Exception while processing request: %s".formatted(e.getMessage());
                fail(new RequestQueueException(request, msg, e));
            }
        }
    }

    private void fail(RequestQueueException exception) {
        log.error(exception.getMessage());
        var request = exception.getRequest();
        if (request != null) {
            request.fail(exception);
        }
    }

    void execute(RequestContext context)  {
        var request = context.getRequest();
        try {
            log.debug("Executing request to {}", request.getUrl());
            var response = (ResponseEntity) restTemplate.exchange(
                request.getUrl(),
                request.getMethod(),
                request.getEntity(),
                request.getResponseType()
            );

            request.complete(response);
        } catch (Exception e) {
            if (!request.isRetryIfFails() || request.retriedTimes() == request.getRetryTimes()) {
                request.fail(e);
            } else {
                log.debug("Request to {} failed. Will retry. Reason: {}",
                    request.getUrl(), e.getMessage());
                request.retried();
                requestQueue.schedule(request, request.getRetryWaitSeconds());
            }
        }
    }

    @Override
    public void kill() {
      super.interrupt();
    }
}