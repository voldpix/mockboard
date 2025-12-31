package dev.mockboard.scheduler;

import dev.mockboard.storage.cache.BoardCacheStore;
import dev.mockboard.storage.cache.MockRuleCacheStore;
import dev.mockboard.storage.repo.BoardRepository;
import dev.mockboard.storage.repo.MockRuleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BoardCleanupScheduler {

    private final BoardRepository boardRepository;
    private final MockRuleRepository mockRuleRepository;

    private final BoardCacheStore boardCacheStore;
    private final MockRuleCacheStore mockRuleCacheStore;

    // Delete all DB data and cache at 03:00 UTC
    @Scheduled(cron = "0 0 3 * * * ")
    public void cleanupOldBoards() {
        boardRepository.deleteAll();
        mockRuleRepository.deleteAll();

        boardCacheStore.evictAllBoardEntries();
        mockRuleCacheStore.evictAllMockRuleEntries();
    }
}
