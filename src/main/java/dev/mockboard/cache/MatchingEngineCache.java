package dev.mockboard.cache;

import dev.mockboard.cache.config.CaffeineEntityCache;
import dev.mockboard.common.engine.PathMatchingEngine;
import org.springframework.stereotype.Component;

@Component
public class MatchingEngineCache extends CaffeineEntityCache<PathMatchingEngine> {

    public MatchingEngineCache() {
        super(10_000, 30);
    }
}
