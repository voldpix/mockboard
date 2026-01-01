package dev.mockboard.storage.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import dev.mockboard.core.common.domain.dto.MockRuleDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class MockRuleCacheStore {

    private static final int MAX_CACHE_SIZE = 10_000;
    private static final int CACHE_EXPIRATION_AFTER_WRITE_MINUTES = 30;

    // apiKey as key
    private final Cache<String, List<MockRuleDto>> mockRules = Caffeine.newBuilder()
            .maximumSize(MAX_CACHE_SIZE)
            .expireAfterAccess(Duration.ofMinutes(CACHE_EXPIRATION_AFTER_WRITE_MINUTES))
            .build();

    public void initMockRulesCache(String apiKey, List<MockRuleDto> mockRuleDtos) {
        log.debug("init mock rules cache for apiKey={}", apiKey);
        mockRules.put(apiKey, mockRuleDtos);
    }

    public void addMockRuleToCache(String apiKey, MockRuleDto mockRule) {
        var cachedRules = getMockRules(apiKey);
        if (CollectionUtils.isEmpty(cachedRules)) {
            var mockRulesToCache = new ArrayList<MockRuleDto>();
            mockRulesToCache.add(mockRule);
            mockRules.put(apiKey, mockRulesToCache);
        } else {
            cachedRules.add(mockRule);
            mockRules.put(apiKey, cachedRules);
        }
    }

    public List<MockRuleDto> getMockRules(String apiKey) {
        return mockRules.getIfPresent(apiKey);
    }

    public void evictAllMockRuleEntries() {
        log.info("Evict all mock rule entries");
        mockRules.invalidateAll();
    }
}
