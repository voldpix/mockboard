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

    // apiKey as key
    private final Cache<String, List<MockRuleDto>> mockRules = Caffeine.newBuilder()
            .maximumSize(10_000)
            .expireAfterAccess(Duration.ofMinutes(30))
            .build();

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
