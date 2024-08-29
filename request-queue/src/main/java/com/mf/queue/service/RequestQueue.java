package com.mf.queue.service;

import com.mf.queue.entity.Request;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Request queue.
 */
public class RequestQueue {

    private final LinkedBlockingDeque<Request<?, ?>> queue = new LinkedBlockingDeque<>();

    /**
     * Submit the specified request for execution.
     *
     * @param request  request to submit
     *
     * @throws InterruptedException if interrupted while waiting
     */
    public void submit(Request<?, ?> request) throws InterruptedException {
        queue.put(request);
    }

    /**
     * Submit the specified request for execution as the first one.
     *
     * @param request  request to submit
     */
    public void submitFirst(Request<?, ?> request) {
        queue.addFirst(request);
    }

    /**
     * Take request for execution. Waits for available requests.
     *
     * @return request
     *
     * @throws InterruptedException if interrupted while waiting
     */
     public Request<?, ?> take() throws InterruptedException {
         return queue.take();
     }
}