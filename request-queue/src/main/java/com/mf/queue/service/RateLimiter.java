package com.mf.queue.service;

import com.mf.queue.entity.Request;

/**
 * Request rate limiter.
 */
public interface RateLimiter {

    /**
     * Tell whether the specified request should be allowed or denied.
     *
     * @param request  request to check
     *
     * @return true if request is allowed, false otherwise
     */
    boolean allowed(Request<?, ?> request);
}