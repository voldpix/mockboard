package dev.mockboard.cache;

import dev.mockboard.cache.config.CaffeineEntityCache;
import dev.mockboard.common.domain.dto.MockRuleDto;
import org.springframework.stereotype.Component;

import java.util.List;

import static dev.mockboard.Constants.DEFAULT_CACHE_EXP_AFTER_ACCESS_MIN;
import static dev.mockboard.Constants.DEFAULT_CACHE_MAX_ENTRIES;

@Component
public class MockRuleCache extends CaffeineEntityCache<List<MockRuleDto>> {

    public MockRuleCache() {
        super(DEFAULT_CACHE_MAX_ENTRIES, DEFAULT_CACHE_EXP_AFTER_ACCESS_MIN);
    }
}
