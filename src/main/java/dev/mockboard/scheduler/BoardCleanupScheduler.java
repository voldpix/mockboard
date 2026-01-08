package dev.mockboard.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
//@Component
@RequiredArgsConstructor
public class BoardCleanupScheduler {

//    private final BoardCacheStore boardCacheStore;
//    private final MockRuleCacheStore mockRuleCacheStore;
//    private final MockExecutionCacheStore mockExecutionCacheStore;
//
//    private final BoardCreationRateLimiter boardCreationRateLimiter;
//    private final MockExecutionRateLimiter mockExecutionRateLimiter;
//
//    // Delete all DB data and cache at 03:00 UTC
//    @Scheduled(cron = "0 0 3 * * * ")
//    public void cleanupBoards() {
//        log.info("Starting daily cleanup...");
//        boardCacheStore.evictAll();
//        mockRuleCacheStore.evictAll();
//        mockExecutionCacheStore.evictAll();
//
//        boardCreationRateLimiter.evictAll();
//        mockExecutionRateLimiter.evictAll();
//
//        log.info("Daily cleanup complete.");
//    }
}
