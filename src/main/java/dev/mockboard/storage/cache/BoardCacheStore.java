package dev.mockboard.storage.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import dev.mockboard.core.common.domain.dto.BoardDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
public class BoardCacheStore {

    private static final int MAX_CACHE_SIZE = 10_000;
    private static final int CACHE_EXPIRATION_AFTER_WRITE_MINUTES = 30;

    // boardId as key, boardDto as value
    private final Cache<String, BoardDto> boards = Caffeine.newBuilder()
            .maximumSize(MAX_CACHE_SIZE)
            .expireAfterAccess(Duration.ofMinutes(CACHE_EXPIRATION_AFTER_WRITE_MINUTES))
            .build();

    // apiKey as key, boardId as value (for mock execution lookups)
    private final Cache<String, String> boardsIdByApiKey = Caffeine.newBuilder()
            .maximumSize(MAX_CACHE_SIZE)
            .expireAfterAccess(Duration.ofMinutes(CACHE_EXPIRATION_AFTER_WRITE_MINUTES))
            .build();

    public void initBoardCache(String boardId, BoardDto boardDto) {
        log.info("init board cache for boardId={}", boardId);
        boards.put(boardId, boardDto);
        boardsIdByApiKey.put(boardDto.getApiKey(), boardDto.getId());
    }

    public void initBoardIdByApiKey(String boardId, String apiKey) {
        log.debug("init board cache for boardId={} by apiKey={}", boardId, apiKey);
        boardsIdByApiKey.put(boardId, apiKey);
    }

    public BoardDto getBoardCache(String boardId) {
        return boards.getIfPresent(boardId);
    }

    public BoardDto getBoardCacheByApiKey(String apiKey) {
        var boardId = getBoardIdByApiKey(apiKey);
        if (boardId == null) return null;
        return getBoardCache(boardId);
    }

    public String getBoardIdByApiKey(String apiKey) {
        return boardsIdByApiKey.getIfPresent(apiKey);
    }

    public void updateBoardCache(String boardId, BoardDto boardDto) {
        log.info("update board cache for boardId={}", boardId);
        boards.put(boardId, boardDto);
    }

    public void evictByBoardId(String boardId) {
        var boardCache = getBoardCache(boardId);
        boardsIdByApiKey.invalidate(boardCache.getApiKey());
        boards.invalidate(boardId);
    }

    public void evictAllBoardEntries() {
        log.info("Evict all board entries");
        boards.invalidateAll();
        boardsIdByApiKey.invalidateAll();
    }
}
