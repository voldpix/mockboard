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

    // boardId as key
    private final Cache<String, BoardDto> boards = Caffeine.newBuilder()
            .maximumSize(10_000)
            .expireAfterAccess(Duration.ofMinutes(30))
            .build();

    public void initBoardCache(String boardId, BoardDto boardDto) {
        log.info("init board cache for boardId={}", boardId);
        boards.put(boardId, boardDto);
    }

    public BoardDto getBoardCache(String boardId) {
        return boards.getIfPresent(boardId);
    }

    public void updateBoardCache(String boardId, BoardDto boardDto) {
        log.info("update board cache for boardId={}", boardId);
        boards.put(boardId, boardDto);
    }

    public void evictAllBoardEntries() {
        log.info("Evict all board entries");
        boards.invalidateAll();
    }
}
