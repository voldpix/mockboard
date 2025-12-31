package dev.mockboard.service;

import dev.mockboard.core.engine.PathMatchingEngine;
import dev.mockboard.storage.cache.BoardCacheStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class MockExecutionService {

    // todo: use cache to avoid concurrency issues
    // or as it is read only and it is ok for concurrent map
    private final Map<String, PathMatchingEngine> enginesByApi = new ConcurrentHashMap<>();
    private final BoardCacheStore boardCacheStore;

//    OLD CODE, TO BE REWORKED AND LINKED
//    public void rebuildEngine(String apiKey) {
//        var engine = new PathMatchingEngine();
//        var rules = boardCacheStore.getMockRulesCache(apiKey);
//        if (!CollectionUtils.isEmpty(rules)) {
//            rules.forEach(rule -> engine.register(rule.getPath(), rule.getId()));
//        }
//        enginesByApi.put(apiKey, engine);
//    }
//
//    public Optional<MockRuleCacheObj> findMatchingRule(String apiKey, String path) {
//        var engine = enginesByApi.get(apiKey);
//        if (engine == null) {
//            rebuildEngine(apiKey);
//            engine = enginesByApi.get(apiKey);
//        }
//
//        return engine.match(path)
//                .flatMap(mockId -> {
//                    var rules = boardCacheStore.getMockRulesCache(apiKey);
//                    return rules.stream()
//                            .filter(rule -> rule.getId().equals(mockId))
//                            .findFirst();
//                });
//    }
}
