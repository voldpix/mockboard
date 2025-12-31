package dev.mockboard.core.common.ratelimiter;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import dev.mockboard.core.common.exception.RateLimitExceededException;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class BoardCreationRateLimiter {

    private static final int MAX_BOARDS_PER_HOUR_PER_IP = 5;

    private final Cache<String, AtomicInteger> creationCounts = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofHours(1))
            .build();

    public void checkLimit(String ip) {
        var count = creationCounts.get(ip, k -> new AtomicInteger(0));
        assert count != null;
        if (count.incrementAndGet() > MAX_BOARDS_PER_HOUR_PER_IP) {
            throw new RateLimitExceededException("Too many boards.. chill");
        }
    }
}
