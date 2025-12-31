package dev.mockboard.core.common.ratelimiter;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import dev.mockboard.core.common.exception.RateLimitExceededException;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class MockExecutionRateLimiter {

    private static final int MAX_REQUESTS_PER_MINUTE_PER_APIKEY = 20;

    private final Cache<String, AtomicInteger> requestCounts = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofMinutes(1))
            .build();

    public void checkLimit(String apiKey) {
        var count = requestCounts.get(apiKey, k -> new AtomicInteger(0));
        assert count != null;
        if (count.incrementAndGet() > MAX_REQUESTS_PER_MINUTE_PER_APIKEY) {
            throw new RateLimitExceededException("Too many requests.. chill");
        }
    }
}
