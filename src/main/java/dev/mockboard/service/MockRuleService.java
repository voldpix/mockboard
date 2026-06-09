package dev.mockboard.service;

import dev.mockboard.Constants;
import dev.mockboard.common.domain.dto.BoardDto;
import dev.mockboard.common.domain.dto.MockRuleDto;
import dev.mockboard.common.domain.response.IdResponse;
import dev.mockboard.common.exception.BadRequestException;
import dev.mockboard.common.exception.NotFoundException;
import dev.mockboard.common.utils.IdGenerator;
import dev.mockboard.common.utils.JsonUtils;
import dev.mockboard.common.validator.MockRuleValidator;
import dev.mockboard.repository.BoardRepository;
import dev.mockboard.repository.model.Board;
import dev.mockboard.repository.model.MockRule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@RequiredArgsConstructor
public class MockRuleService {

    private final MockRuleValidator mockRuleValidator;
    private final BoardRepository boardRepository;

    public IdResponse createMockRule(BoardDto boardDto, MockRuleDto mockRuleDto) {
        log.debug("creating mock rule for boardId={}", boardDto.getId());
        mockRuleValidator.validateMockRule(mockRuleDto);

        mockRuleDto.setId(IdGenerator.generateId());
        mockRuleDto.setBoardId(boardDto.getId());
        mockRuleDto.setHeaders(JsonUtils.minify(mockRuleDto.getHeaders()));
        mockRuleDto.setBody(JsonUtils.minify(mockRuleDto.getBody()));
        mockRuleDto.setTimestamp(Instant.now());
        mockRuleDto.compilePattern();

        var mockRule = toModel(mockRuleDto);
        var updatedBoard = boardRepository.updateById(boardDto.getId(), board -> {
            var rules = mutableRules(board);
            if (rules.size() >= Constants.MAX_MOCK_RULES) {
                throw new BadRequestException("Maximum number of mock rules exceeded. Allowed: " + Constants.MAX_MOCK_RULES);
            }

            rules.add(mockRule);
            board.setMockRules(rules);
            return board;
        });
        if (updatedBoard.isEmpty()) {
            throw new NotFoundException("Board not found by id: " + boardDto.getId());
        }

        log.info("Mock rule added to board: {}", boardDto.getId());
        return new IdResponse(mockRule.getId());
    }

    public List<MockRuleDto> getMockRules(BoardDto boardDto) {
        var persistedMockRules = boardRepository.findMockRulesByBoardIdOrderByTimestampDesc(boardDto.getId());
        if (persistedMockRules == null || persistedMockRules.isEmpty()) {
            return Collections.emptyList();
        }

        return persistedMockRules.stream()
                .map(this::toDto)
                .peek(MockRuleDto::compilePattern)
                .toList();
    }

    public IdResponse updateMockRule(BoardDto boardDto, String mockRuleId, MockRuleDto mockRuleDto) {
        log.debug("updating mock rule={} for boardId={}", mockRuleId, boardDto.getId());
        mockRuleValidator.validateMockRule(mockRuleDto);

        var updatedBoard = boardRepository.updateById(boardDto.getId(), board -> {
            var rules = mutableRules(board);
            for (var i = 0; i < rules.size(); i++) {
                var existing = rules.get(i);
                if (!existing.getId().equals(mockRuleId)) {
                    continue;
                }

                rules.set(i, MockRule.builder()
                        .id(existing.getId())
                        .boardId(existing.getBoardId())
                        .method(mockRuleDto.getMethod())
                        .path(mockRuleDto.getPath())
                        .headers(JsonUtils.minify(mockRuleDto.getHeaders()))
                        .body(JsonUtils.minify(mockRuleDto.getBody()))
                        .statusCode(mockRuleDto.getStatusCode())
                        .delay(mockRuleDto.getDelay())
                        .timestamp(existing.getTimestamp())
                        .build());
                board.setMockRules(rules);
                return board;
            }

            throw new NotFoundException("Mock rule not found for id: " + mockRuleId);
        });
        if (updatedBoard.isEmpty()) {
            throw new NotFoundException("Board not found by id: " + boardDto.getId());
        }

        log.info("Mock rule: {} updated for board: {}", mockRuleId, boardDto.getId());
        return new IdResponse(mockRuleId);
    }

    public void deleteMockRule(BoardDto boardDto, String mockRuleId) {
        log.info("Delete mock rule={} for boardId={}", mockRuleId, boardDto.getId());
        var removed = new AtomicBoolean(false);
        var updatedBoard = boardRepository.updateById(boardDto.getId(), board -> {
            var rules = mutableRules(board);
            removed.set(rules.removeIf(rule -> rule.getId().equals(mockRuleId)));
            board.setMockRules(rules);
            return board;
        });

        if (updatedBoard.isEmpty() || !removed.get()) {
            log.info("Nothing to delete, mock rule not found for id: {}", mockRuleId);
            return;
        }

        log.info("Mock rule deleted: {}", mockRuleId);
    }

    private List<MockRule> mutableRules(Board board) {
        return board.getMockRules() == null
                ? new LinkedList<>()
                : new LinkedList<>(board.getMockRules());
    }

    private MockRule toModel(MockRuleDto dto) {
        return MockRule.builder()
                .id(dto.getId())
                .boardId(dto.getBoardId())
                .method(dto.getMethod())
                .path(dto.getPath())
                .headers(dto.getHeaders())
                .body(dto.getBody())
                .statusCode(dto.getStatusCode())
                .delay(dto.getDelay())
                .timestamp(dto.getTimestamp())
                .build();
    }

    private MockRuleDto toDto(MockRule mockRule) {
        return MockRuleDto.builder()
                .id(mockRule.getId())
                .boardId(mockRule.getBoardId())
                .method(mockRule.getMethod())
                .path(mockRule.getPath())
                .headers(mockRule.getHeaders())
                .body(mockRule.getBody())
                .statusCode(mockRule.getStatusCode())
                .delay(mockRule.getDelay())
                .timestamp(mockRule.getTimestamp())
                .build();
    }
}
