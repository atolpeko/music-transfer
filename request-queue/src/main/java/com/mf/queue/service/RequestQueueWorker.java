package com.mf.queue.service;

/**
 * Waits for requests to be submitted in the request queue
 * and executes them respecting request limits.
 */
public interface RequestQueueWorker {

    /**
     * Start this worker.
     */
    void start();

    /**
     * Stop this worker.
     */
    void stop();
}