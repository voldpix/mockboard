package dev.mockboard.cache;

import dev.mockboard.cache.config.CaffeineEntityCache;
import dev.mockboard.common.domain.dto.BoardDto;
import org.springframework.stereotype.Component;

import static dev.mockboard.Constants.DEFAULT_CACHE_EXP_AFTER_ACCESS_MIN;
import static dev.mockboard.Constants.DEFAULT_CACHE_MAX_ENTRIES;

@Component
public class BoardCache extends CaffeineEntityCache<BoardDto> {
    public BoardCache() {
        super(DEFAULT_CACHE_MAX_ENTRIES, DEFAULT_CACHE_EXP_AFTER_ACCESS_MIN);
    }
}
