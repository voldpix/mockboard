package dev.mockboard.storage.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import dev.mockboard.core.engine.PathMatchingEngine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
public class MockExecutionCacheStore {

    private static final int MAX_CACHE_SIZE = 10_000;
    private static final int CACHE_EXPIRATION_AFTER_WRITE_MINUTES = 30;

    // apiKey as key
    private final Cache<String, PathMatchingEngine> enginesByApiKey = Caffeine.newBuilder()
            .maximumSize(MAX_CACHE_SIZE)
            .expireAfterAccess(Duration.ofMinutes(CACHE_EXPIRATION_AFTER_WRITE_MINUTES))
            .build();

    public void addEngineCache(String apiKey, PathMatchingEngine engine) {
        log.debug("Adding engine cache for apiKey {}", apiKey);
        enginesByApiKey.put(apiKey, engine);
    }

    public PathMatchingEngine getEngineByApiKey(String apiKey) {
        log.debug("Get engine cache for apiKey {}", apiKey);
        return enginesByApiKey.getIfPresent(apiKey);
    }

    public void evictByApiKey(String apiKey) {
        enginesByApiKey.invalidate(apiKey);
    }

    public void evictAll() {
        enginesByApiKey.invalidateAll();
    }
}
