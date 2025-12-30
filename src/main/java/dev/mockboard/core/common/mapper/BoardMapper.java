package dev.mockboard.core.common.mapper;

import dev.mockboard.core.common.doc.BoardDoc;
import dev.mockboard.core.common.doc.MockRule;
import dev.mockboard.core.common.domain.cache.BoardCacheObj;
import dev.mockboard.core.common.domain.cache.MockRuleCacheObj;
import dev.mockboard.core.common.domain.dto.BoardDto;
import dev.mockboard.core.common.domain.dto.MockRuleDto;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BoardMapper {

    private final ModelMapper modelMapper;

    public BoardCacheObj mapBoardDocToBoardCacheObj(BoardDoc doc) {
        return modelMapper.map(doc, BoardCacheObj.class);
    }

    public BoardDto mapBoardDocToBoardDto(BoardDoc doc) {
        return modelMapper.map(doc, BoardDto.class);
    }

    public BoardDto mapBoardCacheObjToBoardDto(BoardCacheObj obj) {
        return modelMapper.map(obj, BoardDto.class);
    }

    public MockRuleCacheObj mapMockRuleToMockRuleCacheObj(MockRule mockRule) {
        return modelMapper.map(mockRule, MockRuleCacheObj.class);
    }

    public MockRuleDto mapMockRuleCacheObjToMockRuleDto(MockRuleCacheObj obj) {
        return modelMapper.map(obj, MockRuleDto.class);
    }

    public MockRule mapMockRuleDtoToMockRule(MockRuleDto dto) {
        return modelMapper.map(dto, MockRule.class);
    }

    public MockRuleDto mapMockRuleToMockRuleDto(MockRule obj) {
        return modelMapper.map(obj, MockRuleDto.class);
    }
}
