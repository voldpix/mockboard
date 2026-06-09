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
import dev.mockboard.repository.MockRuleRepository;
import dev.mockboard.repository.model.MockRule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class MockRuleService {

    private final MockRuleValidator mockRuleValidator;
    private final MockRuleRepository mockRuleRepository;

    public IdResponse createMockRule(BoardDto boardDto, MockRuleDto mockRuleDto) {
        var existingMockRules = getMockRules(boardDto);
        if (existingMockRules.size() >= Constants.MAX_MOCK_RULES) {
            throw new BadRequestException("Maximum number of mock rules exceeded. Allowed: " + Constants.MAX_MOCK_RULES);
        }

        log.debug("creating mock rule for boardId={}", boardDto.getId());
        mockRuleValidator.validateMockRule(mockRuleDto);

        mockRuleDto.setId(IdGenerator.generateId());
        mockRuleDto.setBoardId(boardDto.getId());
        mockRuleDto.setHeaders(JsonUtils.minify(mockRuleDto.getHeaders()));
        mockRuleDto.setBody(JsonUtils.minify(mockRuleDto.getBody()));
        mockRuleDto.setTimestamp(Instant.now());
        mockRuleDto.compilePattern();

        var mockRule = toModel(mockRuleDto);
        mockRuleRepository.save(mockRule);
        log.info("Mock rule added bo board: {}", boardDto.getId());
        return new IdResponse(mockRule.getId());
    }

    public List<MockRuleDto> getMockRules(BoardDto boardDto) {
        var persistedMockRules = mockRuleRepository.findByBoardIdOrderByTimestampDesc(boardDto.getId());
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

        var mockRuleDtos = getMockRules(boardDto);
        var existingDto = mockRuleDtos.stream()
                .filter(m -> m.getId().equals(mockRuleId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Mock rule not found for id: " + mockRuleId));

        existingDto.setMethod(mockRuleDto.getMethod());
        existingDto.setPath(mockRuleDto.getPath());
        existingDto.setHeaders(JsonUtils.minify(mockRuleDto.getHeaders()));
        existingDto.setBody(JsonUtils.minify(mockRuleDto.getBody()));
        existingDto.setStatusCode(mockRuleDto.getStatusCode());
        existingDto.setDelay(mockRuleDto.getDelay());
        existingDto.compilePattern();

        var mockRule = toModel(existingDto);
        mockRuleRepository.save(mockRule);

        log.info("Mock rule: {} updated for board: {}", mockRuleId, boardDto.getId());
        return new IdResponse(mockRuleId);
    }

    public void deleteMockRule(BoardDto boardDto, String mockRuleId) {
        log.info("Delete mock rule={} for boardId={}", mockRuleId, boardDto.getId());
        var mockRules = getMockRules(boardDto);
        var match = mockRules.stream().filter(m -> m.getId().equals(mockRuleId)).findFirst().orElse(null);
        if (match == null) {
            log.info("Nothing to delete, mock rule not found for id: {}", mockRuleId);
            return;
        }

        mockRuleRepository.deleteById(mockRuleId);
        log.info("Mock rule deleted: {}", mockRuleId);
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
