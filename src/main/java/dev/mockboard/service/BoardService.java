package dev.mockboard.service;

import dev.mockboard.core.common.doc.BoardDoc;
import dev.mockboard.core.common.domain.dto.BoardDto;
import dev.mockboard.core.common.domain.dto.IdResponse;
import dev.mockboard.core.common.domain.dto.MockRuleDto;
import dev.mockboard.core.common.exception.NotFoundException;
import dev.mockboard.core.common.mapper.BoardMapper;
import dev.mockboard.core.utils.StringUtils;
import dev.mockboard.storage.cache.BoardCacheStore;
import dev.mockboard.storage.repo.BoardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardService {

    private static final int BOARD_ID_LENGTH = 25;
    private static final int API_KEY_LENGTH = 16;
    private static final int OWNER_TOKEN_LENGTH = 32;
    private static final int MOCK_RULE_ID_LENGTH = 6;

    private final BoardRepository boardRepository;
    private final BoardCacheStore boardCacheStore;
    private final BoardMapper boardMapper;

    public BoardDto createBoard() {
        var boardDoc = new BoardDoc();
        boardDoc.setBoardId(StringUtils.generate(BOARD_ID_LENGTH));
        boardDoc.setApiKey(StringUtils.generate(API_KEY_LENGTH));
        boardDoc.setOwnerToken(StringUtils.generate(OWNER_TOKEN_LENGTH));

        var now = LocalDateTime.now();
        boardDoc.setCreatedAt(now);
        boardDoc.setUpdatedAt(now);
        var stored =  boardRepository.save(boardDoc);
        boardCacheStore.initBoardCache(boardDoc.getId(), boardMapper.mapBoardDocToBoardCacheObj(stored));
        return boardMapper.mapBoardDocToBoardDto(stored);
    }

    public IdResponse addMockRule(String boardId, MockRuleDto mockRuleDto) {
        // todo: validate mock rule dto

        var boardDoc = getBoardDoc(boardId);
        mockRuleDto.setId(StringUtils.generate(MOCK_RULE_ID_LENGTH));
        boardDoc.addMockRule(boardMapper.mapMockRuleDtoToMockRule(mockRuleDto));
        var persisted = boardRepository.save(boardDoc);

        var mockForCache = persisted.getMockRules().stream()
                .filter(mockRule -> mockRule.getId().equals(mockRuleDto.getId()))
                .findFirst()
                .orElse(null);
        if (mockForCache == null) {
            throw new IllegalArgumentException("Unable to persist MockRule");
        }
        boardCacheStore.addMockRuleCache(boardDoc.getApiKey(), boardMapper.mapMockRuleToMockRuleCacheObj(mockForCache));
        return new IdResponse(mockForCache.getId());
    }

    public List<MockRuleDto> getMockRules(String boardId) {
        var boardDoc = getBoardDoc(boardId);
        return boardDoc.getMockRules().stream()
                .map(boardMapper::mapMockRuleToMockRuleDto)
                .toList();
    }

    public void deleteMockRule(String boardId, String mockId) {
        var boardDoc = getBoardDoc(boardId);
        boardDoc.removeMockRule(mockId);
        boardRepository.save(boardDoc);
        boardCacheStore.removeMockRuleCache(boardDoc.getApiKey(), mockId);
    }

    private BoardDoc getBoardDoc(String boardId) {
        var boardDocOpt = boardRepository.findByBoardId(boardId);
        if (boardDocOpt.isEmpty()) {
            throw new NotFoundException("Board not found");
        }

        return boardDocOpt.get();
    }
}
