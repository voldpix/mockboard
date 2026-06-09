package dev.mockboard.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.mockboard.Constants;
import dev.mockboard.common.domain.MockExecutionResult;
import dev.mockboard.common.domain.RequestMetadata;
import dev.mockboard.common.domain.dto.BoardDto;
import dev.mockboard.common.domain.dto.MockRuleDto;
import dev.mockboard.common.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class MockExecutionService {

    public static final String CONTENT_TYPE_HEADER = "Content-Type";
    public static final String JSON_CONTENT_TYPE = "application/json";

    private final ObjectMapper objectMapper;
    private final MockRuleService mockRuleService;
    private final TemplateFakerService templateFakerService;

    public MockExecutionResult execute(String boardId, RequestMetadata metadata) {
        // it is safe, unless mockRuleService.getMockRules(boardDto) changes
        var boardDto = BoardDto.builder().id(boardId).build();
        var mockRule = findMatchingRule(boardDto, metadata.mockPath(), metadata.method()).orElse(null);
        var statusCode = mockRule != null ? mockRule.getStatusCode() : 200;
        var body = determineResponseBody(mockRule);
        var headers = buildHeaders(mockRule);
        applyDelay(mockRule);

        return new MockExecutionResult(mockRule, headers, body, statusCode);
    }

    private Optional<MockRuleDto> findMatchingRule(BoardDto boardDto, String path, String method) {
        var mockRules = mockRuleService.getMockRules(boardDto);
        if (mockRules == null || mockRules.isEmpty()) {
            return Optional.empty();
        }
        var normalizedPath = StringUtils.removeTrailingSlash(path);
        return mockRules.stream()
                .filter(r -> r.getMethod().equalsIgnoreCase(method))
                .filter(r -> r.matches(normalizedPath))
                .min((r1, r2) -> {
                    if (!Objects.equals(r1.getWildcardCount(), r2.getWildcardCount())) {
                        return Integer.compare(r1.getWildcardCount(), r2.getWildcardCount());
                    }
                    return Integer.compare(r2.getPathLength(), r1.getPathLength());
                });
    }

    private String determineResponseBody(MockRuleDto mockRule) {
        if (mockRule == null) {
            return Constants.DEFAULT_EXECUTION_RESPONSE;
        }

        var body = mockRule.getBody();
        return (body == null || body.isEmpty())
                ? "{}"
                : templateFakerService.processTemplates(body);
    }

    private Map<String, List<String>> buildHeaders(MockRuleDto mockRule) {
        var headers = new LinkedHashMap<String, List<String>>();
        if (mockRule != null && mockRule.getHeaders() != null && !mockRule.getHeaders().isEmpty()) {
            try {
                var typeRef = new TypeReference<Map<String, String>>() {};
                var headersMap = objectMapper.readValue(mockRule.getHeaders(), typeRef);
                headersMap.forEach((key, value) -> addHeader(headers, key, value));
            } catch (Exception e) {
                log.warn("Failed to parse headers, using default", e);
                addHeader(headers, CONTENT_TYPE_HEADER, JSON_CONTENT_TYPE);
            }
        } else {
            addHeader(headers, CONTENT_TYPE_HEADER, JSON_CONTENT_TYPE);
        }

        return headers;
    }

    private void addHeader(Map<String, List<String>> headers, String key, String value) {
        headers.computeIfAbsent(key, ignored -> new ArrayList<>()).add(value);
    }

    private void applyDelay(MockRuleDto mockRule) {
        if (mockRule != null && mockRule.getDelay() > 0) {
            try {
                log.debug("Delaying [{}] for {}ms", Thread.currentThread(), mockRule.getDelay());
                Thread.sleep(mockRule.getDelay());
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                log.warn("Delay interrupted", ex);
            }
        }
    }
}
