package com.mf.queue.service.impl;

import com.mf.queue.entity.Request;
import com.mf.queue.exception.InvalidUrlException;
import com.mf.queue.service.RateLimiter;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import lombok.Builder;
import lombok.RequiredArgsConstructor;

import org.springframework.data.redis.core.RedisTemplate;

/**
 * Limits requests for individual services based on the
 * set time window and the number of requests per window.
 */
@Builder
@RequiredArgsConstructor
public class TimeWindowRateLimiter implements RateLimiter {

    private final RedisTemplate<String, String> redisTemplate;
    private final int requestsPerWindow;
    private final int timeWindowInSeconds;

    @Override
    public boolean allowed(Request<?, ?> request) {
        var key = getKey(request);
        var lockValue = UUID.randomUUID().toString();
        try {
            if (!lock(key, lockValue, 1500)) {
                return false;
            }

            var currTime = Instant.now().getEpochSecond();
            redisTemplate.opsForZSet().removeRangeByScore(key, 0, currTime - timeWindowInSeconds);
            var requestCount = redisTemplate.opsForZSet().zCard(key);
            if (requestCount == null) {
                requestCount = 0L;
            }

            if (requestCount < requestsPerWindow) {
                redisTemplate.opsForZSet().add(key, String.valueOf(currTime), currTime);
                redisTemplate.expire(key, timeWindowInSeconds, TimeUnit.SECONDS);
                return true;
            } else {
                return false;
            }
        } finally {
            releaseLock(key, lockValue);
        }
    }

    private String getKey(Request<?, ?> request) {
        var prefix = "rate_limit:";
        try {
            return prefix + request.getHost();
        } catch (InvalidUrlException e) {
            return prefix + "unknown";
        }
    }

    private boolean lock(String key, String lockValue, int forMillis) {
        var lockKey = "lock:" + key;
        var success = redisTemplate.opsForValue()
            .setIfAbsent(lockKey, lockValue, forMillis, TimeUnit.MILLISECONDS);
        return Boolean.TRUE.equals(success);
    }

    private void releaseLock(String key, String lockValue) {
        var lockKey = "lock:" + key;
        var value = redisTemplate.opsForValue().get(lockKey);
        if (value != null && value.equals(lockValue)) {
            redisTemplate.delete(lockKey);
        }
    }
}