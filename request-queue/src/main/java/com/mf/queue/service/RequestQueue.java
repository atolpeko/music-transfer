package com.mf.queue.service;

import com.mf.queue.entity.Request;
import com.mf.queue.exception.RequestQueueException;
import com.mf.queue.valueobject.RequestContext;

import java.util.concurrent.LinkedBlockingDeque;

/**
 * Request queue.
 */
public class RequestQueue {

    private final LinkedBlockingDeque<RequestContext> queue = new LinkedBlockingDeque<>();

    /**
     * Submit the specified request for immediate execution.
     *
     * @param request  request to submit
     */
    public void submit(Request<?, ?> request) {
        try {
            var context = RequestContext.builder()
                .request(request)
                .timeoutMillis(0)
                .build();
            queue.put(context);
        } catch (InterruptedException e) {
            request.fail(new RequestQueueException(request, "Thread interrupted", e));
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Schedule the specified request for execution.
     *
     * @param request  request to schedule
     */
    public void schedule(Request<?, ?> request, int timeoutMillis) {
        try {
            var context = RequestContext.builder()
                .request(request)
                .timeoutMillis(timeoutMillis)
                .build();
            queue.put(context);
        } catch (InterruptedException e) {
            request.fail(new RequestQueueException(request, "Thread interrupted", e));
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Take request for execution. Waits for available requests.
     *
     * @return request
     *
     * @throws InterruptedException if interrupted while waiting for available request
     */
     public RequestContext take() throws InterruptedException {
         return queue.take();
     }
}