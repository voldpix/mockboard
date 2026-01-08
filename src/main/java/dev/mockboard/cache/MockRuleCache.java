package dev.mockboard.cache;

import dev.mockboard.cache.config.CaffeineEntityCache;
import dev.mockboard.common.domain.dto.MockRuleDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MockRuleCache extends CaffeineEntityCache<List<MockRuleDto>> {

    public MockRuleCache() {
        super(10_000, 30);
    }
}
