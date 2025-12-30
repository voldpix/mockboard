package dev.mockboard.storage.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import dev.mockboard.core.common.domain.cache.BoardCacheObj;
import dev.mockboard.core.common.domain.cache.MockRuleCacheObj;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class BoardCacheStore {

    // boardId as key
    private final Cache<String, BoardCacheObj> boards = Caffeine.newBuilder()
            .maximumSize(10_000)
            .expireAfterAccess(Duration.ofMinutes(30))
            .build();

    // apiKey as key
    private final Cache<String, List<MockRuleCacheObj>> mockRules = Caffeine.newBuilder()
            .maximumSize(10_000)
            .expireAfterAccess(Duration.ofMinutes(30))
            .build();

    public void initBoardCache(String boardId, BoardCacheObj boardCacheObj) {
        log.info("init board cache for boardId={}", boardId);
        boards.put(boardId, boardCacheObj);
    }

    public BoardCacheObj getBoardCache(String boardId) {
        return boards.getIfPresent(boardId);
    }

    public void updateBoardCache(String boardId, BoardCacheObj boardCacheObj) {
        log.info("update board cache for boardId={}", boardId);
        boards.put(boardId, boardCacheObj);
    }

    public void addMockRuleCache(String apiKey, MockRuleCacheObj mockRuleCacheObj) {
        log.info("add mock rule cache for apiKey={}", apiKey);
        var cachedRules = mockRules.getIfPresent(apiKey);
        if (CollectionUtils.isEmpty(cachedRules)) {
            log.debug("create new mock rule list for apiKey={}", apiKey);
            cachedRules = new ArrayList<>();
            cachedRules.add(mockRuleCacheObj);
        } else {
            cachedRules.add(mockRuleCacheObj);
        }
        mockRules.put(apiKey, cachedRules);
    }

    public List<MockRuleCacheObj> getMockRulesCache(String apiKey) {
        return mockRules.getIfPresent(apiKey);
    }

    public void removeMockRuleCache(String apiKey, String mockId) {
        log.info("remove mock rule cache for apiKey={}", apiKey);
        var cachedRules = mockRules.getIfPresent(apiKey);
        if (CollectionUtils.isEmpty(cachedRules)) {
            log.debug("no mock rules found by apiKeu={}", apiKey);
            return;
        }

        log.debug("mocks size Before remove: {}", cachedRules.size());
        cachedRules.removeIf(rule -> rule.getId().equals(mockId));
        mockRules.put(apiKey, cachedRules);
        log.debug("mocks size After remove: {}", cachedRules.size());
    }
}
