package dev.mockboard.cache;

import dev.mockboard.cache.config.CaffeineEntityCache;
import dev.mockboard.common.domain.dto.BoardDto;
import org.springframework.stereotype.Component;

@Component
public class BoardCache extends CaffeineEntityCache<BoardDto> {
    public BoardCache() {
        super(10_000, 30);
    }
}
