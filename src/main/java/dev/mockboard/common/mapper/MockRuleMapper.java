package dev.mockboard.common.mapper;

import dev.mockboard.repository.model.MockRule;
import dev.mockboard.common.domain.dto.MockRuleDto;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MockRuleMapper {

    private final ModelMapper modelMapper;

    public MockRule mapMockRuleDtoToMockRuleDoc(MockRuleDto mockRuleDto) {
        return modelMapper.map(mockRuleDto, MockRule.class);
    }

    public MockRuleDto mapMockRuleDocToMockRuleDto(MockRule mockRule) {
        return modelMapper.map(mockRule, MockRuleDto.class);
    }
}
