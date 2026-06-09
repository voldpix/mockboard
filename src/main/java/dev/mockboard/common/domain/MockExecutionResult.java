package dev.mockboard.common.domain;

import dev.mockboard.common.domain.dto.MockRuleDto;

import java.util.List;
import java.util.Map;

public record MockExecutionResult(
        MockRuleDto matchingMockRuleDto,
        Map<String, List<String>> headers,
        String responseBody,
        int statusCode) {}
